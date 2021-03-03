package com.reachfree.dailyexpense.ui.budget

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepository
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.ExpenseBudgetResponse
import com.reachfree.dailyexpense.data.response.ExpenseByCategoryResponse
import com.reachfree.dailyexpense.data.response.ExpenseByCategoryWithBudgetResponse
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-22
 * Time: 오후 9:20
 */
@HiltViewModel
class ExpenseBudgetViewModel @Inject constructor(
    private val repository: ExpenseBudgetRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    var date = MutableLiveData<List<Long>>()

    val expenseBudgetList = Transformations.switchMap(date) { data ->
        repository.getAllExpenseBudgetLiveData(data[0], data[1])
    }

}