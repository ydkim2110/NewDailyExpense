package com.reachfree.dailyexpense.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.dao.ExpenseBudgetDao
import com.reachfree.dailyexpense.data.model.CategoryExpenseByDate
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.data.model.TransactionEntity
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오전 10:29
 */
class ExpenseBudgetRepositoryImpl @Inject constructor(
    private val expenseBudgetDao: ExpenseBudgetDao
) : ExpenseBudgetRepository {

    override suspend fun insertExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        return expenseBudgetDao.insertExpenseBudget(expenseBudget)
    }
    override suspend fun updateExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        return expenseBudgetDao.updateExpenseBudget(expenseBudget)
    }

    override suspend fun updateExpenseBudget(amount: BigDecimal, categoryId: String) {
        return expenseBudgetDao.updateExpenseBudget(amount, categoryId)
    }

    override suspend fun deleteExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        return expenseBudgetDao.deleteExpenseBudget(expenseBudget)
    }

    override suspend fun deleteExpenseBudgetByCategoryId(categoryId: String) {
        return expenseBudgetDao.deleteExpenseBudgetByCategoryID(categoryId)
    }

    override suspend fun getExpenseBudget(categoryId: String): Result<ExpenseBudgetEntity> {
        return try {
            val result = expenseBudgetDao.getExpenseBudget(categoryId)
            Result.Success(result)
        } catch (e: Exception) {
            Timber.d("error $e")
            Result.Error("notExist")
        }
    }

    override suspend fun isExistExpenseBudget(categoryId: String): Int {
        return expenseBudgetDao.isExistExpenseBudget(categoryId)
    }

    override suspend fun getAllExpenseBudget(
        startDate: Long,
        endDate: Long
    ): Result<List<ExpenseByCategoryWithBudget>> {
        val result = expenseBudgetDao.getAllExpenseBudget(startDate, endDate)
        return Result.Success(result)
    }

    override fun getExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<ExpenseByCategoryWithBudget> {
        return expenseBudgetDao.getExpenseBudgetLiveData(startDate, endDate, categoryId)
    }

    override fun getAllExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<ExpenseByCategoryWithBudget>> {
        return expenseBudgetDao.getAllExpenseBudgetLiveData(startDate, endDate)
    }

    override fun getAllCategoryTransactionLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<List<CategoryExpenseByDate>> {
        return expenseBudgetDao.getAllCategoryTransactionLiveData(startDate, endDate, categoryId)
    }

    override fun getAllTransactionByCategoryLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<List<TransactionEntity>> {
        return expenseBudgetDao.getAllTransactionByCategoryLiveData(startDate, endDate, categoryId)
    }

}