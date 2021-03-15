package com.reachfree.dailyexpense.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.INTERVAL
import com.reachfree.dailyexpense.util.Constants.PATTERN
import com.reachfree.dailyexpense.util.Constants.PAYMENT
import com.reachfree.dailyexpense.util.Constants.TYPE
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 12:04
 */
@Entity(
    tableName = "transaction_table"
)
@Parcelize
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var description: String = "",
    var amount: BigDecimal? = null,
    var type: Int = TYPE.EXPENSE.ordinal,
    var payment: Int = PAYMENT.CREDIT.ordinal,
    var pattern: Int = PATTERN.NORMAL.ordinal,
    var categoryId: String = "",
    var subCategoryId: String = "",
    var registerDate: Long = Date().time,
    var interval: Int = INTERVAL.NON_RECURRING.ordinal
) : Parcelable