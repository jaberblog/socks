package com.jeansock.manager.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
    }

    private val formatter = DecimalFormat("#,###", symbols)

    fun formatWithCommas(value: Long): String {
        return formatter.format(value)
    }

    fun formatWithCommas(value: Int): String {
        return formatter.format(value.toLong())
    }

    fun normalizeDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                in '۰'..'۹' -> sb.append((ch.code - '۰'.code + '0'.code).toChar())
                in '٠'..'٩' -> sb.append((ch.code - '٠'.code + '0'.code).toChar())
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun parseLongSafe(input: String): Long {
        val clean = normalizeDigits(input).replace(",", "").replace(" ", "").trim()
        return clean.toLongOrNull() ?: 0L
    }

    fun parseIntSafe(input: String): Int {
        val clean = normalizeDigits(input).replace(",", "").replace(" ", "").trim()
        return clean.toIntOrNull() ?: 0
    }
}
