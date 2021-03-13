package com.reachfree.dailyexpense.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.ui.dashboard.payment.PaymentChartModel
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountChartModel

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 12:03
 */
@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transaction_table WHERE id LIKE :id")
    suspend fun deleteById(id: Int)

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE id LIKE :id
    """)
    suspend fun getTransactionById(id: Int): TransactionEntity

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
        ORDER BY registerDate DESC
    """)
    suspend fun getAllTransactionsByDate(startDate: Long, endDate: Long):  List<TransactionEntity>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
        ORDER BY registerDate DESC
    """)
    fun getThisMonthTransactions(startDate: Long, endDate: Long):  LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        ORDER BY registerDate DESC
    """)
    fun getThisMonthExpenseTransactions(startDate: Long, endDate: Long):  LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 1
        ORDER BY registerDate DESC
    """)
    fun getThisMonthIncomeTransactions(startDate: Long, endDate: Long):  LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        ORDER BY :orderByColumn DESC
    """)
    fun getTransactionSortedBy(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        orderByColumn: String
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
        AND type IN (:type)
        ORDER BY registerDate DESC
    """)
    fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
            AND type IN (:type)
            AND payment IN (:payment)
        ORDER BY registerDate DESC
    """)
    fun getTransactionByPaymentSortedByDate(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        ORDER BY registerDate DESC
    """)
    fun getTransactionSortedByDate(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
        AND type IN (:type)
        ORDER BY amount DESC
    """)
    fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
            AND type IN (:type)
            AND payment IN (:payment)
        ORDER BY amount DESC
    """)
    fun getTransactionByPaymentSortedByAmount(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        ORDER BY amount DESC
    """)
    fun getTransactionSortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        AND subCategoryId LIKE :subCategoryId
        ORDER BY amount DESC
    """)
    fun getTransactionBySubCategorySortedByAmount(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: String,
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        AND subCategoryId IN (:subCategoryId)
        ORDER BY amount DESC
    """)
    fun getTransactionBySubCategorySortedByAmountIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>,
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        AND subCategoryId IN (:subCategoryId)
        ORDER BY registerDate DESC
    """)
    fun getTransactionBySubCategorySortedByDateIn(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String,
        subCategoryId: Array<String>
    ): LiveData<List<TransactionEntity>>


    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
        AND type IN (:type)
        ORDER BY categoryId, subCategoryId
    """)
    fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate
            AND type IN (:type)
            AND payment IN (:payment)
        ORDER BY categoryId, subCategoryId
    """)
    fun getTransactionByPaymentSortedByCategory(
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ): LiveData<List<TransactionEntity>>

    @Query("""
        SELECT *
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        ORDER BY categoryId, subCategoryId, description
    """)
    fun getTransactionSortedByCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<TransactionEntity>>


    @Query("""
        SELECT subCategoryId AS subCategoryId,
            SUM(amount) AS sumBySubCategory,
            COUNT(amount) AS countBySubCategory
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        GROUP BY subCategoryId
        ORDER BY sumBySubCategory DESC
    """)
    suspend fun getSubCategoryGroup(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): List<ExpenseBySubCategory>

    @Query("""
        SELECT subCategoryId AS subCategoryId,
            SUM(amount) AS sumBySubCategory,
            COUNT(amount) AS countBySubCategory
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        AND categoryId LIKE :categoryId
        GROUP BY subCategoryId
        ORDER BY sumBySubCategory DESC
    """)
    fun getSubCategoryGroupLiveData(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ): LiveData<List<ExpenseBySubCategory>>

    @Query("""
        SELECT categoryId AS categoryId, 
            SUM(amount) AS sumByCategory,
            COUNT(amount) AS countByCategory
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        GROUP BY categoryId
    """)
    suspend fun getThisMonthTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
    ): List<ExpenseByCategory>

    @Query("""
        SELECT categoryId AS categoryId, 
            SUM(amount) AS sumByCategory,
            COUNT(amount) AS countByCategory
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        AND pattern IN (:pattern)
        GROUP BY categoryId
    """)
    fun getTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
    ): LiveData<List<ExpenseByCategory>>


    @Query("""
        SELECT categoryId AS categoryId, 
            SUM(amount) AS sumByCategory,
            COUNT(amount) AS countByCategory
        FROM transaction_table
        WHERE registerDate BETWEEN :startDate AND :endDate 
        AND type LIKE 0
        GROUP BY categoryId
    """)
    fun getThisMonthTransactionByPatternTotalAndCategory(
        startDate: Long,
        endDate: Long
    ): List<ExpenseByCategory>

    @Query("SELECT * FROM transaction_table WHERE registerDate BETWEEN :startDate AND :endDate")
    fun getRecentTransactions(startDate: Long, endDate: Long): LiveData<List<TransactionEntity>>


    @Query("""
        SELECT
            STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch')) AS date,
            SUM(amount) AS amount,
            COUNT(amount) AS count,
            type
        FROM TRANSACTION_TABLE
        WHERE registerDate BETWEEN :startDate AND :endDate
        GROUP BY type, STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch')) 
    """)
    fun getAllTransactionByTypeLiveData(
        startDate: Long,
        endDate: Long
    ) : LiveData<List<TotalAmountChartModel>>

    @Query("""
        SELECT
            STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch')) AS date,
            SUM(amount) AS amount,
            COUNT(amount) AS count,
            payment
        FROM TRANSACTION_TABLE
        WHERE registerDate BETWEEN :startDate AND :endDate
        AND type LIKE 0
        GROUP BY payment, STRFTIME('%Y-%m', datetime(registerDate/1000,  'unixepoch')) 
    """)
    fun getAllTransactionByPaymentLiveData(
        startDate: Long,
        endDate: Long
    ) : LiveData<List<PaymentChartModel>>

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}