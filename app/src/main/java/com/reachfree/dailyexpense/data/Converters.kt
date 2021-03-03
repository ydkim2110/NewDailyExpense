package com.reachfree.dailyexpense.data

import androidx.room.TypeConverter
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 12:35
 */
class Converters {

    @TypeConverter
    fun fromLong(value: Long): BigDecimal {
        return BigDecimal(value).divide(BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): Long {
        return value.multiply(BigDecimal(100)).longValueExact()
    }

}