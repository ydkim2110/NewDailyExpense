package com.reachfree.dailyexpense.ui.dashboard

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.TransactionListResponse
import com.reachfree.dailyexpense.util.AppUtils
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
 * Date: 2021-02-21
 * Time: 오후 10:15
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    var dateForMonthly = MutableLiveData<List<Long>>()
    var dateForRecent = MutableLiveData<List<Long>>()

    val monthlyTransaction = Transformations.switchMap(dateForMonthly) { data ->
        repository.getThisMonthTransactions(data[0], data[1])
    }
    val recentTransaction = Transformations.switchMap(dateForRecent) { data ->
        repository.getRecentTransactions(data[0], data[1])
    }
}