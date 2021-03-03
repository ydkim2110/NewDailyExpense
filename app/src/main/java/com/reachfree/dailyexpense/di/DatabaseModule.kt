package com.reachfree.dailyexpense.di

import android.R.attr.category
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reachfree.dailyexpense.data.LocalDatabase
import com.reachfree.dailyexpense.data.dao.ExpenseBudgetDao
import com.reachfree.dailyexpense.data.dao.TransactionDao
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepository
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepositoryImpl
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.repository.TransactionRepositoryImpl
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.LOCAL_DATABASE_NAME
import com.reachfree.dailyexpense.util.DispatcherProvider
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.toMillis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton


/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 12:20
 */
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

    @Singleton
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, LocalDatabase::class.java, LOCAL_DATABASE_NAME)
        .addCallback(callback)
        .build()

    @Singleton
    @Provides
    fun provideTransactionDao(database: LocalDatabase) = database.transactionDao()

    @Singleton
    @Provides
    fun provideExpenseBudgetDao(database: LocalDatabase) = database.expenseBudgetDao()

    @Singleton
    @Provides
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository =
        TransactionRepositoryImpl(transactionDao)

    @Singleton
    @Provides
    fun provideExpenseBudgetRepository(expenseBudgetDao: ExpenseBudgetDao): ExpenseBudgetRepository =
        ExpenseBudgetRepositoryImpl(expenseBudgetDao)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

    private val callback = object : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dateTime1 = LocalDateTime.now().toMillis()
            val dateTime2 = LocalDateTime.now().plusDays(1).toMillis()
            val dateTime3 = LocalDateTime.now().plusDays(2).toMillis()
            val dateTime4 = LocalDateTime.now().plusDays(3).toMillis()
            val dateTime5 = LocalDateTime.now().plusDays(4).toMillis()
            val dateTime6 = LocalDateTime.now().plusDays(5).toMillis()
            val dateTime7 = LocalDateTime.now().plusDays(6).toMillis()
            val dateTime8 = LocalDateTime.now().minusDays(1).toMillis()
            val dateTime9 = LocalDateTime.now().minusDays(2).toMillis()
            val dateTime10 = LocalDateTime.now().minusDays(3).toMillis()
            val dateTime11 = LocalDateTime.now().minusDays(4).toMillis()
            val dateTime12 = LocalDateTime.now().minusDays(5).toMillis()
            val dateTime13 = LocalDateTime.now().minusDays(6).toMillis()
            val dateTime14 = LocalDateTime.now().minusDays(7).toMillis()

            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('이삭 토스트', 300000, 0, 0, 0, ':food&drink', ':breakfast', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('순대국', 800000, 0, 0, 0, ':food&drink', ':lunch', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('스타벅스', 410000, 0, 0, 0, ':food&drink', ':coffee&beverage', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('이삭 토스트2', 300000, 0, 1, 0, ':food&drink', ':breakfast', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('배당', 1500000, 1, 2, 0, ':interest&dividend', ':interest&dividend', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('택시', 550000, 0, 0, 2, ':transportation', ':taxi', ${dateTime1})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('감자탕', 1450000, 0, 0, 2, ':food&drink', ':dinner', ${dateTime2})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('커피', 1450000, 0, 1, 1, ':food&drink', ':coffee&beverage', ${dateTime3})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('술값', 3450000, 0, 0, 0, ':food&drink', ':dinner', ${dateTime3})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('신발구매', 15000000, 0, 0, 0, ':clothing&beauty', ':shoes', ${dateTime4})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('영화', 1800000, 0, 0, 0, ':entertainment', ':movie', ${dateTime5})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('택시', 450000, 0, 0, 2, ':transportation', ':taxi', ${dateTime6})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('월급', 400000000, 1, 2, 3, ':salary', ':salary', ${dateTime7})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('이자', 34200, 1, 2, 3, ':interest&dividend', ':interest&dividend', ${dateTime8})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('스타벅스', 410000, 0, 0, 0, ':food&drink', ':coffee&beverage', ${dateTime8})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('순대국', 800000, 0, 0, 0, ':food&drink', ':lunch', ${dateTime8})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('월급', 1300000000, 1, 2, 3, ':salary', ':salary', ${dateTime9})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('피씨방', 1550000, 0, 0, 2, ':entertainment', ':game', ${dateTime10})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('인터넷 강의', 16550000, 0, 0, 0, ':education', ':lecture', ${dateTime10})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('버스', 1100000, 0, 0, 0, ':transportation', ':bus', ${dateTime10})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('냉면', 750000, 0, 1, 0, ':food&drink', ':lunch', ${dateTime11})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('관리비', 150034, 0, 0, 0, ':household', ':maintenance_charge', ${dateTime12})"
            )
            db.execSQL(
                "INSERT INTO transaction_table (description, amount, type, payment, pattern, categoryId, subCategoryId, registerDate)" +
                        " VALUES ('숙박', 1500000, 0, 0, 0, ':travel', ':inbound', ${dateTime13})"
            )

//            for (i in Constants.expenseCategories().indices) {
//                db.execSQL(
//                    "INSERT INTO expense_budget_table (categoryId, amount, registerDate) VALUES(?, ?, ?)",
//                    arrayOf<Any>(Constants.expenseCategories()[i].id, 15000000, Date().time))
//            }
        }
    }
}