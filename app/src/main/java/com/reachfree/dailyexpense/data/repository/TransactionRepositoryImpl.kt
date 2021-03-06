package com.reachfree.dailyexpense.data.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.dao.TransactionDao
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.ui.dashboard.payment.PaymentChartModel
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountChartModel
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 3:08
 */
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun insert(transaction: TransactionEntity) {
        return transactionDao.insert(transaction)
    }

    override suspend fun update(transaction: TransactionEntity) {
        return transactionDao.update(transaction)
    }

    override suspend fun delete(transaction: TransactionEntity) {
        return transactionDao.delete(transaction)
    }

    override suspend fun deleteById(id: Int) {
        return transactionDao.deleteById(id)
    }

    override suspend fun deleteAll() {
        return transactionDao.deleteAll()
    }

    override fun getCountTodayTransaction(startDate: Long, endDate: Long): Int {
        return transactionDao.getCountTodayTransaction(startDate, endDate)
    }

    override suspend fun getAllTransactionsByDate(
        startDate: Long,
        endDate: Long
    ): Result<List<TransactionEntity>> {
        val result = transactionDao.getAllTransactionsByDate(startDate, endDate)
        return Result.Success(result)
    }

    override suspend fun getTransactionById(id: Int): Result<TransactionEntity> {
        val result = transactionDao.getTransactionById(id)
        return Result.Success(result)
    }

    override fun getThisMonthTransactions(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getThisMonthTransactions(startDate, endDate)
    }

    override fun getThisMonthExpenseTransactions(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getThisMonthExpenseTransactions(startDate, endDate)
    }

    override fun getThisMonthIncomeTransactions(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getThisMonthIncomeTransactions(startDate, endDate)
    }

    override suspend fun getThisMonthTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray
    ): Result<List<ExpenseByCategory>> {
        val result = transactionDao.getThisMonthTransactionByPatternToCategory(startDate, endDate, pattern)
        return Result.Success(result)
    }

    override fun getTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray
    ): LiveData<List<ExpenseByCategory>> {
        return transactionDao.getTransactionByPatternToCategory(startDate, endDate, pattern)
    }

    override fun getTransactionSortedBy(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        orderByColumn: String
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedBy(startDate, endDate, pattern, categoryId, orderByColumn)
    }

    override fun getRecentTransactions(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(startDate, endDate)
    }

    override fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByDate(startDate, endDate, type)
    }

    override fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByDate(startDate, endDate, pattern, categoryId)
    }

    override fun getTransactionByPaymentSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionByPaymentSortedByDate(startDate, endDate, type, payment)
    }

    override fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByAmount(startDate, endDate, type)
    }

    override fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByAmount(startDate, endDate, pattern, categoryId)
    }

    override fun getTransactionByPaymentSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionByPaymentSortedByAmount(startDate, endDate, type, payment)
    }

    override fun getTransactionBySubCategorySortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: String
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionBySubCategorySortedByAmount(startDate, endDate, pattern, categoryId, subCategoryId)
    }

    override fun getTransactionBySubCategorySortedByAmountIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionBySubCategorySortedByAmountIn(startDate, endDate, pattern, categoryId, subCategoryId)
    }

    override fun getTransactionBySubCategorySortedByDateIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionBySubCategorySortedByDateIn(startDate, endDate, pattern, categoryId, subCategoryId)
    }

    override fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByCategory(startDate, endDate, type)
    }

    override fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionSortedByCategory(startDate, endDate, pattern, categoryId)
    }

    override fun getTransactionByPaymentSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionByPaymentSortedByCategory(startDate, endDate, type, payment)
    }

    override suspend fun getSubCategoryGroup(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): Result<List<ExpenseBySubCategory>> {
        val result = transactionDao.getSubCategoryGroup(startDate, endDate, pattern, categoryId)
        return Result.Success(result)
    }

    override fun getSubCategoryGroupLiveData(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<ExpenseBySubCategory>> {
        return transactionDao.getSubCategoryGroupLiveData(startDate, endDate, pattern, categoryId)
    }

    override fun getAllTransactionByTypeLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<TotalAmountChartModel>> {
        return transactionDao.getAllTransactionByTypeLiveData(startDate, endDate)
    }

    override fun getAllTransactionByPaymentLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<PaymentChartModel>> {
        return transactionDao.getAllTransactionByPaymentLiveData(startDate, endDate)
    }

    override suspend fun checkpoint(): Int {
        return transactionDao.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }

}