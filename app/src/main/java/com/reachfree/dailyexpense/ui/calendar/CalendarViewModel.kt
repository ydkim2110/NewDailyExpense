package com.reachfree.dailyexpense.ui.calendar

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.TransactionListResponse
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.YearMonth
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-21
 * Time: 오후 4:43
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    var date = MutableLiveData<List<Long>>()

    val transactionList = Transformations.switchMap(date) { data ->
        repository.getThisMonthTransactions(data[0], data[1])
    }

}