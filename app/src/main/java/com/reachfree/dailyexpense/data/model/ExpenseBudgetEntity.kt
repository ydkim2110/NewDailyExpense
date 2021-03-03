package com.reachfree.dailyexpense.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오전 10:22
 */
@Entity(
    tableName = "expense_budget_table"
)
data class ExpenseBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var categoryId: String = "",
    var amount: BigDecimal? = null,
    var registerDate: Long = Date().time
)