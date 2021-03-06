package com.reachfree.dailyexpense.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reachfree.dailyexpense.data.dao.ExpenseBudgetDao
import com.reachfree.dailyexpense.data.dao.TransactionDao
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.model.TransactionEntity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 12:17
 */
@Database(
    entities = [TransactionEntity::class, ExpenseBudgetEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun expenseBudgetDao(): ExpenseBudgetDao

    val databaseExecutor: ExecutorService = Executors.newSingleThreadExecutor()

}