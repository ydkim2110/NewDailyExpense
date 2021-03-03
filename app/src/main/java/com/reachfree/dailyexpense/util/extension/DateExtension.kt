package com.reachfree.dailyexpense.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오전 11:01
 */
fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atZone(zone)?.toInstant()?.toEpochMilli()

fun LocalDate.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atStartOfDay(zone).toInstant().toEpochMilli()