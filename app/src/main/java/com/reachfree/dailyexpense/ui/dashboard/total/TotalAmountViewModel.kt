package com.reachfree.dailyexpense.ui.dashboard.total

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-04
 * Time: 오전 10:53
 */
@HiltViewModel
class TotalAmountViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {


    val allTransactionByType = MediatorLiveData<List<TotalAmountChartModel>>()

    fun getAllTransactionByTypeLiveData(
        startDate: Long,
        endDate: Long
    ) {
        val transactionByType = repository.getAllTransactionByTypeLiveData(startDate, endDate)

        allTransactionByType.addSource(transactionByType) { result ->
            allTransactionByType.value = result
        }
    }

    val transactionListByType = MediatorLiveData<List<TransactionEntity>>()

    fun getTransactionSortedBy(
        sortType: Constants.SortType,
        startDate: Long,
        endDate: Long,
        type: IntArray
    ) {
        val subCategoryByAmount = repository.getTransactionSortedByAmount(startDate, endDate, type)
        val subCategoryByDate = repository.getTransactionSortedByDate(startDate, endDate, type)
        val subCategoryByCategory = repository.getTransactionSortedByCategory(startDate, endDate, type)

        transactionListByType.addSource(subCategoryByAmount) { result ->
            if (sortType == Constants.SortType.AMOUNT) {
                result?.let { transactionListByType.value = it }
            }
        }
        transactionListByType.addSource(subCategoryByDate) { result ->
            if (sortType == Constants.SortType.DATE) {
                result?.let { transactionListByType.value = it }
            }
        }
        transactionListByType.addSource(subCategoryByCategory) { result ->
            if (sortType == Constants.SortType.CATEGORY) {
                result?.let { transactionListByType.value = it }
            }
        }

        when (sortType) {
            Constants.SortType.AMOUNT -> subCategoryByAmount.value?.let { transactionListByType.value = it }
            Constants.SortType.DATE -> subCategoryByDate.value?.let { transactionListByType.value = it }
            Constants.SortType.CATEGORY -> subCategoryByCategory.value?.let { transactionListByType.value = it }
        }
    }

}