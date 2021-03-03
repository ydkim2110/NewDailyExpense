package com.reachfree.dailyexpense.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.CategoryExpenseByDate
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.data.model.TransactionEntity

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오전 10:26
 */
@Dao
interface ExpenseBudgetDao {

    @Insert
    suspend fun insertExpenseBudget(expenseBudget: ExpenseBudgetEntity)

    @Update
    suspend fun updateExpenseBudget(expenseBudget: ExpenseBudgetEntity)

    @Delete
    suspend fun deleteExpenseBudget(expenseBudget: ExpenseBudgetEntity)

    @Query("SELECT COUNT(categoryId) FROM expense_budget_table WHERE categoryId LIKE :categoryId")
    suspend fun isExistExpenseBudget(categoryId: String): Int

    @Query("SELECT * FROM expense_budget_table WHERE categoryId LIKE :categoryId")
    suspend fun getExpenseBudget(categoryId: String): ExpenseBudgetEntity

    @Query("DELETE FROM expense_budget_table WHERE categoryId LIKE :categoryId")
    suspend fun deleteExpenseBudgetByCategoryID(categoryId: String)

    @Query("""
        SELECT 
            E.categoryId AS categoryId, 
            (
                SELECT
                    SUM(amount) AS sumByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS sumByCategory,
            (
                SELECT
                    COUNT(amount) AS countByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS countByCategory,
            E.amount AS budgetAmount
        FROM expense_budget_table E
        WHERE categoryId LIKE :categoryId
        GROUP BY E.categoryId
    """)
    fun getExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<ExpenseByCategoryWithBudget>

    @Query("""
        SELECT 
            E.categoryId AS categoryId, 
            (
                SELECT
                    SUM(amount) AS sumByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS sumByCategory,
            (
                SELECT
                    COUNT(amount) AS countByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS countByCategory,
            E.amount AS budgetAmount
        FROM expense_budget_table E
        GROUP BY E.categoryId
    """)
    fun getAllExpenseBudget(
        startDate: Long,
        endDate: Long
    ) : List<ExpenseByCategoryWithBudget>

    @Query("""
        SELECT 
            E.categoryId AS categoryId, 
            (
                SELECT
                    SUM(amount) AS sumByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS sumByCategory,
            (
                SELECT
                    COUNT(amount) AS countByCategory
                FROM transaction_table
                WHERE categoryId = E.categoryId 
                AND registerDate BETWEEN :startDate AND :endDate
                AND type LIKE 0
                GROUP BY categoryId
            ) AS countByCategory,
            E.amount AS budgetAmount
        FROM expense_budget_table E
        GROUP BY E.categoryId
    """)
    fun getAllExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long
    ): LiveData<List<ExpenseByCategoryWithBudget>>

    @Query("""
        SELECT
            STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch')) AS date,
            SUM(amount) AS sumByCategory,
            COUNT(amount) AS countByCategory
        FROM TRANSACTION_TABLE
        WHERE categoryId LIKE :categoryId
        AND type LIKE 0
        AND registerDate BETWEEN :startDate AND :endDate
        GROUP BY STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch'))
    """)
    fun getAllCategoryTransactionLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ) : LiveData<List<CategoryExpenseByDate>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND categoryId LIKE :categoryId
        ORDER BY registerDate DESC
    """)
    fun getAllTransactionByCategoryLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

}