package com.reachfree.dailyexpense.data.repository

import androidx.lifecycle.LiveData
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.ui.dashboard.payment.PaymentChartModel
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountChartModel

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 3:06
 */
interface TransactionRepository {

    suspend fun insert(transaction: TransactionEntity)

    suspend fun update(transaction: TransactionEntity)

    suspend fun delete(transaction: TransactionEntity)

    suspend fun deleteById(id: Int)

    suspend fun deleteAll()

    fun getCountTodayTransaction(startDate: Long, endDate: Long): Int

    suspend fun getAllTransactionsByDate(
        startDate: Long,
        endDate: Long
    ): Result<List<TransactionEntity>>

    suspend fun getTransactionById(id: Int): Result<TransactionEntity>

    fun getThisMonthTransactions(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>>

    fun getThisMonthExpenseTransactions(startDate: Long, endDate: Long): LiveData<List<TransactionEntity>>
    fun getThisMonthIncomeTransactions(startDate: Long, endDate: Long): LiveData<List<TransactionEntity>>
    fun getRecentTransactions(startDate: Long, endDate: Long): LiveData<List<TransactionEntity>>

    suspend fun getThisMonthTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray
    ): Result<List<ExpenseByCategory>>

    fun getTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray
    ): LiveData<List<ExpenseByCategory>>

    fun getTransactionSortedBy(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        orderByColumn: String
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionByPaymentSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionByPaymentSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

    fun getTransactionBySubCategorySortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: String
    ): LiveData<List<TransactionEntity>>

    fun getTransactionBySubCategorySortedByAmountIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>
    ): LiveData<List<TransactionEntity>>

    fun getTransactionBySubCategorySortedByDateIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionByPaymentSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

    suspend fun getSubCategoryGroup(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): Result<List<ExpenseBySubCategory>>

    fun getSubCategoryGroupLiveData(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<ExpenseBySubCategory>>

    fun getAllTransactionByTypeLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TotalAmountChartModel>>

    fun getAllTransactionByPaymentLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<PaymentChartModel>>

    suspend fun checkpoint(): Int
}