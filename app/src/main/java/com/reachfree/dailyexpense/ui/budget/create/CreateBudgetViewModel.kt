package com.reachfree.dailyexpense.ui.budget.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepository
import com.reachfree.dailyexpense.data.response.ExpenseBudgetResponse
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-02
 * Time: 오후 10:29
 */
@HiltViewModel
class CreateBudgetViewModel @Inject constructor(
    private val repository: ExpenseBudgetRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    var date = MutableLiveData<List<Long>>()

    val expenseBudgetList = Transformations.switchMap(date) { data ->
        repository.getAllExpenseBudgetLiveData(data[0], data[1])
    }

    private val _isExistExpenseBudget = MutableLiveData<Int>()
    val isExistExpenseBudget get() = _isExistExpenseBudget

    fun isExistExpenseBudget(
        categoryId: String
    ) {
        viewModelScope.launch(dispatchers.io) {
            isExistExpenseBudget.postValue(repository.isExistExpenseBudget(categoryId))
        }
    }

    private val _selectedExpenseBudget = MutableLiveData<ExpenseBudgetResponse>()
    val selectedExpenseBudget get() = _selectedExpenseBudget

    fun getExpenseBudget(
        categoryId: String
    ) {
        viewModelScope.launch(dispatchers.io) {
            _selectedExpenseBudget.postValue(ExpenseBudgetResponse.loading())
            when (val result = repository.getExpenseBudget(categoryId)) {
                is Result.Success -> {
                    _selectedExpenseBudget.postValue(ExpenseBudgetResponse.success(result.data))
                }
                is Result.Error -> {
                    _selectedExpenseBudget.postValue(ExpenseBudgetResponse.error(result.error))
                }
            }
        }
    }

    fun insertExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        viewModelScope.launch(dispatchers.io) {
            repository.insertExpenseBudget(expenseBudget)
        }
    }

    fun updateExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        viewModelScope.launch(dispatchers.io) {
            repository.updateExpenseBudget(expenseBudget)
        }
    }

    fun updateExpenseBudget(amount: BigDecimal, categoryId: String) {
        viewModelScope.launch(dispatchers.io) {
            repository.updateExpenseBudget(amount, categoryId)
        }
    }
}