package com.reachfree.dailyexpense.ui.calendar.bottomsheet

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.TransactionListResponse
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-21
 * Time: 오후 6:58
 */
@HiltViewModel
class TransactionListBottomSheetViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var _selectedDateTransactions = MutableLiveData<TransactionListResponse>()
    val selectedDateTransactions get() = _selectedDateTransactions

    var date = MutableLiveData<List<Long>>()

    val transactionList = Transformations.switchMap(date) { data ->
        repository.getThisMonthTransactions(data[0], data[1])
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

}