package com.jeansock.manager.data

import android.content.Context
import com.jeansock.manager.model.DashboardStats
import com.jeansock.manager.model.JinEntry
import com.jeansock.manager.model.PeriodStat
import com.jeansock.manager.util.JalaliCalendarHelper
import com.jeansock.manager.util.JalaliDate
import org.json.JSONArray
import org.json.JSONObject

class JinRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPricePerDozen(): Long {
        return prefs.getLong(KEY_PRICE_PER_DOZEN, DEFAULT_PRICE_PER_DOZEN)
    }

    fun setPricePerDozen(price: Long) {
        prefs.edit().putLong(KEY_PRICE_PER_DOZEN, price.coerceAtLeast(0L)).apply()
    }

    fun getEntries(): List<JinEntry> {
        val raw = prefs.getString(KEY_ENTRIES_JSON, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<JinEntry>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    JinEntry(
                        id = obj.optLong("id", System.currentTimeMillis() + i),
                        dozenCount = obj.optInt("dozenCount", 0),
                        jalaliYear = obj.optInt("jalaliYear", 1405),
                        jalaliMonth = obj.optInt("jalaliMonth", 1),
                        jalaliDay = obj.optInt("jalaliDay", 1),
                        pricePerDozen = obj.optLong("pricePerDozen", getPricePerDozen()),
                        totalProfit = obj.optLong("totalProfit", 0L)
                    )
                )
            }
            list.sortedByDescending { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveEntries(entries: List<JinEntry>) {
        val arr = JSONArray()
        for (entry in entries) {
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("dozenCount", entry.dozenCount)
                put("jalaliYear", entry.jalaliYear)
                put("jalaliMonth", entry.jalaliMonth)
                put("jalaliDay", entry.jalaliDay)
                put("pricePerDozen", entry.pricePerDozen)
                put("totalProfit", entry.totalProfit)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_ENTRIES_JSON, arr.toString()).apply()
    }

    fun addEntry(dozenCount: Int, date: JalaliDate): List<JinEntry> {
        val price = getPricePerDozen()
        val profit = dozenCount.toLong() * price
        val newEntry = JinEntry(
            id = System.currentTimeMillis(),
            dozenCount = dozenCount,
            jalaliYear = date.year,
            jalaliMonth = date.month,
            jalaliDay = date.day,
            pricePerDozen = price,
            totalProfit = profit
        )
        val updated = listOf(newEntry) + getEntries()
        saveEntries(updated)
        return updated
    }

    fun deleteEntry(id: Long): List<JinEntry> {
        val updated = getEntries().filterNot { it.id == id }
        saveEntries(updated)
        return updated
    }

    fun computeStats(entries: List<JinEntry>): DashboardStats {
        if (entries.isEmpty()) return DashboardStats()

        val today = JalaliCalendarHelper.today()
        val yesterday = JalaliCalendarHelper.yesterday()
        val (weekStart, weekEnd) = JalaliCalendarHelper.currentWeekRange()

        var todayCount = 0
        var todayProfit = 0L

        var yesterdayCount = 0
        var yesterdayProfit = 0L

        var weekCount = 0
        var weekProfit = 0L

        var totalCount = 0
        var totalProfit = 0L

        for (entry in entries) {
            val entryDate = JalaliDate(entry.jalaliYear, entry.jalaliMonth, entry.jalaliDay)
            totalCount += entry.dozenCount
            totalProfit += entry.totalProfit

            if (entryDate == today) {
                todayCount += entry.dozenCount
                todayProfit += entry.totalProfit
            }
            if (entryDate == yesterday) {
                yesterdayCount += entry.dozenCount
                yesterdayProfit += entry.totalProfit
            }
            if (entryDate >= weekStart && entryDate <= weekEnd) {
                weekCount += entry.dozenCount
                weekProfit += entry.totalProfit
            }
        }

        return DashboardStats(
            today = PeriodStat(todayCount, todayProfit),
            yesterday = PeriodStat(yesterdayCount, yesterdayProfit),
            week = PeriodStat(weekCount, weekProfit),
            total = PeriodStat(totalCount, totalProfit)
        )
    }

    fun exportToJsonString(): String {
        val entries = getEntries()
        val entriesArray = JSONArray()
        for (entry in entries) {
            entriesArray.put(
                JSONObject().apply {
                    put("id", entry.id)
                    put("dozenCount", entry.dozenCount)
                    put("jalaliYear", entry.jalaliYear)
                    put("jalaliMonth", entry.jalaliMonth)
                    put("jalaliDay", entry.jalaliDay)
                    put("pricePerDozen", entry.pricePerDozen)
                    put("totalProfit", entry.totalProfit)
                }
            )
        }
        return JSONObject().apply {
            put("app", "JeanSockManager")
            put("version", 1)
            put("exportedAt", JalaliCalendarHelper.today().formatSlash())
            put("pricePerDozen", getPricePerDozen())
            put("entries", entriesArray)
        }.toString(2)
    }

    fun importFromJsonString(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val savedPrice = if (root.has("pricePerDozen")) {
                root.getLong("pricePerDozen")
            } else {
                getPricePerDozen()
            }
            val arr = root.optJSONArray("entries") ?: JSONArray()
            val restoredList = mutableListOf<JinEntry>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val count = obj.optInt("dozenCount", 0)
                val unitPrice = obj.optLong("pricePerDozen", savedPrice)
                val profit = if (obj.has("totalProfit")) {
                    obj.getLong("totalProfit")
                } else {
                    count.toLong() * unitPrice
                }
                restoredList.add(
                    JinEntry(
                        id = obj.optLong("id", System.currentTimeMillis() + i),
                        dozenCount = count,
                        jalaliYear = obj.optInt("jalaliYear", JalaliCalendarHelper.today().year),
                        jalaliMonth = obj.optInt("jalaliMonth", JalaliCalendarHelper.today().month),
                        jalaliDay = obj.optInt("jalaliDay", JalaliCalendarHelper.today().day),
                        pricePerDozen = unitPrice,
                        totalProfit = profit
                    )
                )
            }
            setPricePerDozen(savedPrice)
            saveEntries(restoredList)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val PREFS_NAME = "jean_sock_manager_prefs"
        private const val KEY_PRICE_PER_DOZEN = "price_per_dozen"
        private const val KEY_ENTRIES_JSON = "jin_entries_json"
        const val DEFAULT_PRICE_PER_DOZEN = 200000L
    }
}
