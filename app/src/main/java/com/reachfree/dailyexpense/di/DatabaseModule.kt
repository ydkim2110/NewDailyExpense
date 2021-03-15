package com.reachfree.dailyexpense.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reachfree.dailyexpense.data.LocalDatabase
import com.reachfree.dailyexpense.data.dao.ExpenseBudgetDao
import com.reachfree.dailyexpense.data.dao.TransactionDao
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepository
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepositoryImpl
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.repository.TransactionRepositoryImpl
import com.reachfree.dailyexpense.util.Constants.LOCAL_DATABASE_NAME
import com.reachfree.dailyexpense.util.DispatcherProvider
import com.reachfree.dailyexpense.manager.SessionManager
import com.reachfree.dailyexpense.util.toMillis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
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
        .addMigrations(MIGRATION_1_2)
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

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE transaction_table ADD COLUMN interval INTEGER NOT NULL DEFAULT 0")
        }
    }
}