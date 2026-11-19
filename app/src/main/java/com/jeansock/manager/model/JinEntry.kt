package com.jeansock.manager.model

data class JinEntry(
    val id: Long,
    val dozenCount: Int,
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val jalaliDay: Int,
    val pricePerDozen: Long,
    val totalProfit: Long
) {
    fun formattedJalaliDate(): String {
        val m = jalaliMonth.toString().padStart(2, '0')
        val d = jalaliDay.toString().padStart(2, '0')
        return "$jalaliYear/$m/$d"
    }
}

data class PeriodStat(
    val count: Int = 0,
    val profit: Long = 0L
)

data class DashboardStats(
    val today: PeriodStat = PeriodStat(),
    val yesterday: PeriodStat = PeriodStat(),
    val week: PeriodStat = PeriodStat(),
    val total: PeriodStat = PeriodStat()
)

data class BackupItemInfo(
    val fileName: String,
    val displayPath: String,
    val filePath: String? = null,
    val contentUriString: String? = null
)
