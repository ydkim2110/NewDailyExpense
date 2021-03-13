package com.reachfree.dailyexpense.ui.viewmodel

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.TransactionListResponse
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.DispatcherProvider
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 3:13
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val thisMonthTransaction: LiveData<List<TransactionEntity>>
    val thisMonthExpenseTransaction: LiveData<List<TransactionEntity>>
    val thisMonthInComeTransaction: LiveData<List<TransactionEntity>>
    val recentTransaction: LiveData<List<TransactionEntity>>

    val subCategoryList = MediatorLiveData<List<TransactionEntity>>()

    private var _selectedDateTransactions = MutableLiveData<TransactionListResponse>()
    val selectedDateTransactions get() = _selectedDateTransactions

    init {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)

        val startDate = startOfDay.minusDays(1).toMillis()!!
        val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toMillis()!!

        val startOfMonth = AppUtils.startOfMonth(YearMonth.now())
        val endOfMonth = AppUtils.endOfMonth(YearMonth.now())

        thisMonthTransaction = repository.getThisMonthTransactions(startOfMonth, endOfMonth)
        thisMonthExpenseTransaction = repository.getThisMonthExpenseTransactions(startOfMonth, endOfMonth)
        thisMonthInComeTransaction = repository.getThisMonthIncomeTransactions(startOfMonth, endOfMonth)
        recentTransaction = repository.getRecentTransactions(startDate = startDate, endDate = endDate)
    }

    fun getTransactionSortedBy(
        sortType: Constants.SortType,
        startDate: Long,
        endDate: Long,
        type: IntArray
    ) {
        val subCategoryByAmount = repository.getTransactionSortedByAmount(startDate, endDate, type)
        val subCategoryByDate = repository.getTransactionSortedByDate(startDate, endDate, type)
        val subCategoryByCategory = repository.getTransactionSortedByCategory(startDate, endDate, type)

        subCategoryList.addSource(subCategoryByAmount) { result ->
            if (sortType == Constants.SortType.AMOUNT) {
                result?.let { subCategoryList.value = it }
            }
        }
        subCategoryList.addSource(subCategoryByDate) { result ->
            if (sortType == Constants.SortType.DATE) {
                result?.let { subCategoryList.value = it }
            }
        }
        subCategoryList.addSource(subCategoryByCategory) { result ->
            if (sortType == Constants.SortType.CATEGORY) {
                result?.let { subCategoryList.value = it }
            }
        }

        when (sortType) {
            Constants.SortType.AMOUNT -> subCategoryByAmount.value?.let { subCategoryList.value = it }
            Constants.SortType.DATE -> subCategoryByDate.value?.let { subCategoryList.value = it }
            Constants.SortType.CATEGORY -> subCategoryByCategory.value?.let { subCategoryList.value = it }
        }
    }

    fun getAllTransactionsByDate(
        startDate: Long,
        endDate: Long
    ) {
        viewModelScope.launch(dispatchers.io) {
            _selectedDateTransactions.postValue(TransactionListResponse.loading())
            when (val result = repository.getAllTransactionsByDate(startDate, endDate)) {
                is Result.Success -> {
                    _selectedDateTransactions.postValue(TransactionListResponse.success(result.data))
                }
                is Result.Error -> {
                    _selectedDateTransactions.postValue(TransactionListResponse.error(result.error))
                }
            }
        }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                Timber.d("insertTransaction")
                repository.insert(transaction)
            } catch (e: Exception) {
                Timber.d("ERROR: ${e.message}")
            }
        }
    }

    fun checkpoint() {
        viewModelScope.launch(dispatchers.io) {
            repository.checkpoint()
        }
    }
}