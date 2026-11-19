package com.jeansock.manager.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.jeansock.manager.data.JinRepository
import com.jeansock.manager.model.BackupItemInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    private const val FOLDER_NAME = "jean"

    fun exportBackupToJeanFolder(context: Context, repository: JinRepository): Result<String> {
        val jsonContent = repository.exportToJsonString()
        val timeTag = SimpleDateFormat("HHmmss", Locale.US).format(Date())
        val jalaliTag = JalaliCalendarHelper.today().formatSlash().replace("/", "")
        val fileName = "jean_backup_${jalaliTag}_$timeTag.json"

        // 1. Try direct root storage folder /storage/emulated/0/jean if accessible
        try {
            @Suppress("DEPRECATION")
            val rootStorage = Environment.getExternalStorageDirectory()
            if (rootStorage != null) {
                val rootJeanDir = File(rootStorage, FOLDER_NAME)
                if (rootJeanDir.exists() || rootJeanDir.mkdirs()) {
                    if (rootJeanDir.canWrite()) {
                        val targetFile = File(rootJeanDir, fileName)
                        targetFile.writeText(jsonContent, Charsets.UTF_8)
                        return Result.success("jean/$fileName")
                    }
                }
            }
        } catch (_: Exception) {
            // Fall through to Scoped Storage MediaStore on Android 10+
        }

        // 2. Android 10+ (API 29+): Save via MediaStore into Documents/jean/ (no permissions required!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/$FOLDER_NAME"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                }
                val resolver = context.contentResolver
                val collection = MediaStore.Files.getContentUri("external")
                val uri = resolver.insert(collection, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        out.write(jsonContent.toByteArray(Charsets.UTF_8))
                        out.flush()
                    }
                    return Result.success("Documents/$FOLDER_NAME/$fileName")
                }
            } catch (_: Exception) {
                // Fall through to app external files directory
            }
        }

        // 3. Fallback to app-specific external Documents/jean directory
        return try {
            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val jeanDir = File(baseDir, FOLDER_NAME)
            if (!jeanDir.exists()) jeanDir.mkdirs()
            val outFile = File(jeanDir, fileName)
            outFile.writeText(jsonContent, Charsets.UTF_8)
            Result.success("${jeanDir.absolutePath}/$fileName")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun findAvailableBackups(context: Context): List<BackupItemInfo> {
        val results = mutableListOf<BackupItemInfo>()
        val seenNames = mutableSetOf<String>()

        // Scan /storage/emulated/0/jean and /storage/emulated/0/Documents/jean
        @Suppress("DEPRECATION")
        val extRoot = Environment.getExternalStorageDirectory()
        val candidateDirs = listOfNotNull(
            extRoot?.let { File(it, FOLDER_NAME) },
            extRoot?.let { File(it, "${Environment.DIRECTORY_DOCUMENTS}/$FOLDER_NAME") },
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.let { File(it, FOLDER_NAME) },
            File(context.filesDir, FOLDER_NAME)
        )

        for (dir in candidateDirs) {
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles { f -> f.isFile && f.name.endsWith(".json", ignoreCase = true) }
                files?.sortedByDescending { it.lastModified() }?.forEach { file ->
                    if (seenNames.add(file.name)) {
                        results.add(
                            BackupItemInfo(
                                fileName = file.name,
                                displayPath = "${dir.name}/${file.name}",
                                filePath = file.absolutePath
                            )
                        )
                    }
                }
            }
        }

        // Also query MediaStore on Android 10+ for files in jean folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val collection = MediaStore.Files.getContentUri("external")
                val projection = arrayOf(
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.RELATIVE_PATH
                )
                val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
                val selectionArgs = arrayOf("%$FOLDER_NAME%")
                val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"

                context.contentResolver.query(
                    collection, projection, selection, selectionArgs, sortOrder
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val pathCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol) ?: continue
                        val relPath = cursor.getString(pathCol) ?: FOLDER_NAME
                        if (name.endsWith(".json", ignoreCase = true) && seenNames.add(name)) {
                            val itemUri = Uri.withAppendedPath(collection, id.toString())
                            results.add(
                                BackupItemInfo(
                                    fileName = name,
                                    displayPath = "$relPath$name",
                                    contentUriString = itemUri.toString()
                                )
                            )
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        return results
    }

    fun restoreBackup(context: Context, repository: JinRepository, item: BackupItemInfo): Boolean {
        return try {
            val content = when {
                item.filePath != null -> File(item.filePath).readText(Charsets.UTF_8)
                item.contentUriString != null -> {
                    val uri = Uri.parse(item.contentUriString)
                    context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                        it.readText()
                    } ?: return false
                }
                else -> return false
            }
            repository.importFromJsonString(content)
        } catch (e: Exception) {
            false
        }
    }

    fun restoreFromUri(context: Context, repository: JinRepository, uri: Uri): Boolean {
        return try {
            val content = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                it.readText()
            } ?: return false
            repository.importFromJsonString(content)
        } catch (e: Exception) {
            false
        }
    }
}
