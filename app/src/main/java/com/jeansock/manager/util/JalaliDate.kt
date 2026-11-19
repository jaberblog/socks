package com.jeansock.manager.util

import java.util.Calendar
import java.util.GregorianCalendar

data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<JalaliDate> {

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    fun formatSlash(): String {
        val m = month.toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        return "$year/$m/$d"
    }

    fun formatMonthYear(): String {
        return "${JalaliCalendarHelper.monthName(month)} $year"
    }

    fun formatWeekdayDayMonth(): String {
        val wdIndex = JalaliCalendarHelper.persianWeekdayIndex(year, month, day)
        val wdFullName = JalaliCalendarHelper.fullWeekdayName(wdIndex)
        val mName = JalaliCalendarHelper.monthName(month)
        return "$wdFullName $day $mName"
    }
}

object JalaliCalendarHelper {

    private val MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val WEEKDAY_SHORT_NAMES = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    private val WEEKDAY_FULL_NAMES = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    fun monthName(month: Int): String = MONTH_NAMES.getOrElse(month - 1) { "" }

    fun fullWeekdayName(persianWeekdayIndex: Int): String =
        WEEKDAY_FULL_NAMES.getOrElse(persianWeekdayIndex) { "" }

    fun today(): JalaliDate {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun yesterday(): JalaliDate {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun currentWeekRange(): Pair<JalaliDate, JalaliDate> {
        val cal = Calendar.getInstance()
        // Calendar.DAY_OF_WEEK: SUNDAY=1 ... SATURDAY=7
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysSinceSaturday = if (dow == Calendar.SATURDAY) 0 else dow
        val startCal = (cal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -daysSinceSaturday)
        }
        val endCal = (startCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 6)
        }
        val startJalali = gregorianToJalali(
            startCal.get(Calendar.YEAR),
            startCal.get(Calendar.MONTH) + 1,
            startCal.get(Calendar.DAY_OF_MONTH)
        )
        val endJalali = gregorianToJalali(
            endCal.get(Calendar.YEAR),
            endCal.get(Calendar.MONTH) + 1,
            endCal.get(Calendar.DAY_OF_MONTH)
        )
        return startJalali to endJalali
    }

    fun isJalaliLeapYear(jy: Int): Boolean {
        val breaks = intArrayOf(
            -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
        )
        var bl = breaks.size
        var jp = breaks[0]
        var jump = 0
        for (i in 1 until bl) {
            val jm = breaks[i]
            jump = jm - jp
            if (jy < jm) break
            jp = jm
        }
        var n = jy - jp
        if (jump - n < 6) {
            n = n - jump + ((jump + 4) / 33) * 33
        }
        var leap = (((n + 1) % 33) - 1) % 4
        if (leap == -1) leap = 4
        return leap == 0
    }

    fun daysInJalaliMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isJalaliLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Returns 0 for شنبه (Saturday) through 6 for جمعه (Friday).
     */
    fun persianWeekdayIndex(jy: Int, jm: Int, jd: Int): Int {
        val (gy, gm, gd) = jalaliToGregorian(jy, jm, jd)
        val cal = GregorianCalendar(gy, gm - 1, gd)
        val dow = cal.get(Calendar.DAY_OF_WEEK) // SUNDAY=1 .. SATURDAY=7
        return if (dow == Calendar.SATURDAY) 0 else dow
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gdm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
                ((gy2 + 399) / 400) + gd + gdm[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return JalaliDate(jy, jm, jd)
    }

    fun jalaliToGregorian(jyInput: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val jy = jyInput + 1595
        var days = -355668 + (365 * jy) + ((jy / 33) * 8) + (((jy % 33) + 3) / 4) + jd +
                if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            days -= 1
            gy += 100 * (days / 36524)
            days %= 36524
            if (days >= 365) days += 1
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gd = days + 1
        val isLeap = (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)
        val salA = intArrayOf(
            0, 31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )
        var gm = 0
        while (gm < 13 && gd > salA[gm]) {
            gd -= salA[gm]
            gm++
        }
        return Triple(gy, gm, gd)
    }
}
