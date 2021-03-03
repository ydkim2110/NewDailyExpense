package com.reachfree.dailyexpense.ui.budget.detail

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.model.*
import com.reachfree.dailyexpense.data.repository.ExpenseBudgetRepository
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-01
 * Time: 오후 5:16
 */
@HiltViewModel
class ExpenseBudgetDetailViewModel @Inject constructor(
    private val repository: ExpenseBudgetRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val expenseBudgetByCategoryList = MediatorLiveData<List<CategoryExpenseByDate>>()
    val expenseByCategoryList = MediatorLiveData<List<TransactionEntity>>()
    val expenseBudgetByCategory = MediatorLiveData<ExpenseByCategoryWithBudget>()

    fun getExpenseBudgetLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ) {
        val expenseBudget = repository.getExpenseBudgetLiveData(startDate, endDate, categoryId)
        expenseBudgetByCategory.addSource(expenseBudget) { result ->
            expenseBudgetByCategory.value = result
        }
    }

    fun getAllCategoryTransactionLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ) {
        val subCategory = repository.getAllCategoryTransactionLiveData(startDate, endDate, categoryId)

        expenseBudgetByCategoryList.addSource(subCategory) { result ->
            expenseBudgetByCategoryList.value = result
        }
    }

    fun getAllTransactionByCategoryLiveData(
        startDate: Long,
        endDate: Long,
        categoryId: String
    ) {
        val subCategory = repository.getAllTransactionByCategoryLiveData(startDate, endDate, categoryId)

        expenseByCategoryList.addSource(subCategory) { result ->
            expenseByCategoryList.value = result
        }
    }

    fun deleteExpenseBudget(expenseBudget: ExpenseBudgetEntity) {
        viewModelScope.launch(dispatchers.io) {
            repository.deleteExpenseBudget(expenseBudget)
        }
    }

    fun deleteExpenseBudgetByCategoryId(categoryId: String) {
        viewModelScope.launch(dispatchers.io) {
            repository.deleteExpenseBudgetByCategoryId(categoryId)
        }
    }
}