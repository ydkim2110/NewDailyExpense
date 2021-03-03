package com.reachfree.dailyexpense.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.CategoryExpenseByDate
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.data.model.TransactionEntity

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오전 10:27
 */
interface ExpenseBudgetRepository {

    suspend fun getExpenseBudget(categoryId: String): Result<ExpenseBudgetEntity>
    suspend fun isExistExpenseBudget(categoryId: String): Int
    suspend fun getAllExpenseBudget(startDate: Long, endDate: Long): Result<List<ExpenseByCategoryWithBudget>>
    suspend fun insertExpenseBudget(expenseBudget: ExpenseBudgetEntity)
    suspend fun updateExpenseBudget(expenseBudget: ExpenseBudgetEntity)
    suspend fun deleteExpenseBudget(expenseBudget: ExpenseBudgetEntity)
    suspend fun deleteExpenseBudgetByCategoryId(categoryId: String)

    fun getExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<ExpenseByCategoryWithBudget>

    fun getAllExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<ExpenseByCategoryWithBudget>>

    fun getAllCategoryTransactionLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<List<CategoryExpenseByDate>>

    fun getAllTransactionByCategoryLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<List<TransactionEntity>>
}