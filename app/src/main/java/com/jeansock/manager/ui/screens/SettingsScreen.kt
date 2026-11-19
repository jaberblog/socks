package com.jeansock.manager.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeansock.manager.R
import com.jeansock.manager.data.JinRepository
import com.jeansock.manager.model.BackupItemInfo
import com.jeansock.manager.ui.components.AdBannerSection
import com.jeansock.manager.ui.components.AppHeaderCard
import com.jeansock.manager.ui.components.FloatingPlusButton
import com.jeansock.manager.ui.theme.BackButtonGray
import com.jeansock.manager.ui.theme.BackgroundGray
import com.jeansock.manager.ui.theme.BorderGray
import com.jeansock.manager.ui.theme.CardWhite
import com.jeansock.manager.ui.theme.PrimaryGradientEnd
import com.jeansock.manager.ui.theme.PrimaryGradientStart
import com.jeansock.manager.ui.theme.TextDarkGray
import com.jeansock.manager.ui.theme.TextMediumGray
import com.jeansock.manager.ui.theme.TextPrimaryBlack
import com.jeansock.manager.ui.theme.VazirFontFamily
import com.jeansock.manager.util.BackupManager
import com.jeansock.manager.util.NumberFormatter

@Composable
fun SettingsScreen(
    currentPricePerDozen: Long,
    repository: JinRepository,
    onSavePrice: (Long) -> Unit,
    onBackupRestored: () -> Unit,
    onAddClick: () -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current

    var priceText by remember(currentPricePerDozen) {
        mutableStateOf(NumberFormatter.formatWithCommas(currentPricePerDozen))
    }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var availableBackups by remember { mutableStateOf<List<BackupItemInfo>>(emptyList()) }

    val primaryGradient = Brush.verticalGradient(
        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
    )

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val ok = BackupManager.restoreFromUri(context, repository, uri)
            if (ok) {
                onBackupRestored()
                Toast.makeText(context, "بکاپ با موفقیت بازیابی شد", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "فایل بکاپ معتبر نیست", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text(
                    text = "بازیابی از پوشه jean",
                    fontFamily = VazirFontFamily,
                    fontSize = 15.sp,
                    color = TextPrimaryBlack
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (availableBackups.isEmpty()) {
                        Text(
                            text = "فایل بکاپی در پوشه jean پیدا نشد. می‌توانید فایل JSON را دستی انتخاب کنید:",
                            fontFamily = VazirFontFamily,
                            fontSize = 13.sp,
                            color = TextMediumGray
                        )
                    } else {
                        Text(
                            text = "یکی از فایل‌های بکاپ داخل پوشه jean را انتخاب کنید:",
                            fontFamily = VazirFontFamily,
                            fontSize = 13.sp,
                            color = TextMediumGray
                        )
                        availableBackups.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, PrimaryGradientStart, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val ok = BackupManager.restoreBackup(context, repository, item)
                                        showRestoreDialog = false
                                        if (ok) {
                                            onBackupRestored()
                                            Toast.makeText(
                                                context,
                                                "بکاپ ${item.fileName} بازیابی شد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "خطا در خواندن فایل بکاپ",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = item.displayPath,
                                    fontFamily = VazirFontFamily,
                                    fontSize = 12.sp,
                                    color = TextPrimaryBlack
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        openDocumentLauncher.launch(arrayOf("application/json", "*/*"))
                    }
                ) {
                    Text(
                        text = "انتخاب فایل از حافظه",
                        fontFamily = VazirFontFamily,
                        fontSize = 13.sp,
                        color = PrimaryGradientStart
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(
                        text = "انصراف",
                        fontFamily = VazirFontFamily,
                        fontSize = 13.sp,
                        color = TextMediumGray
                    )
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 1. Top Header Card
        AppHeaderCard(
            title = "تنظیمات",
            onSettingsClick = { /* Already in settings */ }
        )

        // 2. Main Settings Content + Floating Plus Button Overlay
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Label: "قیمت هر جین"
                Text(
                    text = "قیمت هر جین",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextMediumGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price Input Box (48dp height, 1dp #E5E7EB border, 8dp radius)
                BasicTextField(
                    value = priceText,
                    onValueChange = { raw ->
                        val digitsOnly = NumberFormatter.normalizeDigits(raw).filter { it.isDigit() }
                        if (digitsOnly.length <= 12) {
                            val num = digitsOnly.toLongOrNull() ?: 0L
                            priceText = if (digitsOnly.isEmpty()) "" else NumberFormatter.formatWithCommas(num)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontFamily = VazirFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = TextPrimaryBlack,
                        textAlign = TextAlign.Left
                    ),
                    cursorBrush = SolidColor(PrimaryGradientStart),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardWhite)
                                .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row: "برگشت" & "ذخیره" (130dp x 48dp each, 16dp gap, aligned to End/Left in RTL)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "برگشت" button
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackButtonGray)
                            .clickable { onBackToHome() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "برگشت",
                            fontFamily = VazirFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = TextMediumGray
                        )
                    }

                    // "ذخیره" button
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryGradient)
                            .clickable {
                                val parsedPrice = NumberFormatter.parseLongSafe(priceText)
                                onSavePrice(parsedPrice)
                                Toast.makeText(
                                    context,
                                    "قیمت هر جین ذخیره شد",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ذخیره",
                            fontFamily = VazirFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Label: "بکاپ گیری"
                Text(
                    text = "بکاپ گیری",
                    fontFamily = VazirFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextMediumGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Backup & Restore Buttons Row (16dp gap, 48dp height, 1dp #667EEA border, 8dp radius)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right button in RTL: "گرفتن بکاپ" (Save JSON to `jean` folder)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardWhite)
                            .border(1.dp, PrimaryGradientStart, RoundedCornerShape(8.dp))
                            .clickable {
                                val result = BackupManager.exportBackupToJeanFolder(context, repository)
                                result.onSuccess { savedPath ->
                                    Toast.makeText(
                                        context,
                                        "بکاپ JSON در پوشه jean ذخیره شد:
$savedPath",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }.onFailure {
                                    Toast.makeText(
                                        context,
                                        "خطا در ذخیره‌سازی بکاپ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
                                contentDescription = null,
                                tint = TextMediumGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "گرفتن بکاپ",
                                fontFamily = VazirFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = TextMediumGray
                            )
                        }
                    }

                    // Left button in RTL: "بازیابی بکاپ" (Restore JSON from `jean` folder)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardWhite)
                            .border(1.dp, PrimaryGradientStart, RoundedCornerShape(8.dp))
                            .clickable {
                                availableBackups = BackupManager.findAvailableBackups(context)
                                showRestoreDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_upload),
                                contentDescription = null,
                                tint = TextMediumGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بازیابی بکاپ",
                                fontFamily = VazirFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = TextMediumGray
                            )
                        }
                    }
                }
            }

            // Floating Plus Button at bottom-left (End in RTL) matching Figma
            FloatingPlusButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
        }

        // 3. Bottom Ad Banner
        AdBannerSection()
    }
}
