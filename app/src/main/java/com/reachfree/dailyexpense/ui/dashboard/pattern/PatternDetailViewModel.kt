package com.reachfree.dailyexpense.ui.dashboard.pattern

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.ExpenseByCategoryResponse
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 8:59
 */
@HiltViewModel
class PatternDetailViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val transactionByPatternToCategory = MediatorLiveData<List<ExpenseByCategory>>()

    fun getTransactionByPatternToCategory(
        startDate: Long,
        endDate: Long,
        pattern: IntArray
    ) {
        val subCategory = repository.getTransactionByPatternToCategory(startDate, endDate, pattern)

        transactionByPatternToCategory.addSource(subCategory) { result ->
            transactionByPatternToCategory.value = result
        }
    }

}