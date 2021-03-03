package com.reachfree.dailyexpense.ui.dashboard.pattern.category

import androidx.lifecycle.*
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.ExpenseBySubCategoryResponse
import com.reachfree.dailyexpense.util.Constants.SortType
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오후 4:20
 */
@HiltViewModel
class PatternCategoryViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider,

) : ViewModel() {

    private val _thisMonthSubCategoryGroup = MutableLiveData<ExpenseBySubCategoryResponse>()
    val thisMonthSubCategoryGroup get() = _thisMonthSubCategoryGroup

    val subCategorySortBy = MediatorLiveData<List<TransactionEntity>>()
    val subCategoryListLiveData = MediatorLiveData<List<ExpenseBySubCategory>>()

    fun getSubCategoryGroupLiveData(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ) {
        val subCategory = repository.getSubCategoryGroupLiveData(startDate, endDate, pattern, categoryId)

        subCategoryListLiveData.addSource(subCategory) { result ->
            subCategoryListLiveData.value = result
        }
    }

    fun getTransactionBySubCategorySortedBy(
        sortType: SortType,
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ) {
        val subCategoryByAmount = repository.getTransactionSortedByAmount(startDate, endDate, pattern, categoryId)
        val subCategoryByDate = repository.getTransactionSortedByDate(startDate, endDate, pattern, categoryId)
        val subCategoryByCategory = repository.getTransactionSortedByCategory(startDate, endDate, pattern, categoryId)

        subCategorySortBy.addSource(subCategoryByAmount) { result ->
            if (sortType == SortType.AMOUNT) {
                result?.let { subCategorySortBy.value = it }
            }
        }
        subCategorySortBy.addSource(subCategoryByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let { subCategorySortBy.value = it }
            }
        }
        subCategorySortBy.addSource(subCategoryByCategory) { result ->
            if (sortType == SortType.CATEGORY) {
                result?.let { subCategorySortBy.value = it }
            }
        }

        when (sortType) {
            SortType.AMOUNT -> subCategoryByAmount.value?.let { subCategorySortBy.value = it }
            SortType.DATE -> subCategoryByDate.value?.let { subCategorySortBy.value = it }
            SortType.CATEGORY -> subCategoryByCategory.value?.let { subCategorySortBy.value = it }
        }
    }

    fun getSubCategoryGroup(
        startDate: Long,
        endDate: Long,
        pattern: IntArray,
        categoryId: String
    ) {
        viewModelScope.launch(dispatchers.io) {
            _thisMonthSubCategoryGroup.postValue(ExpenseBySubCategoryResponse.loading())

            when (val result = repository.getSubCategoryGroup(startDate, endDate, pattern, categoryId)) {
                is Result.Success -> {
                    _thisMonthSubCategoryGroup.postValue(ExpenseBySubCategoryResponse.success(result.data))
                }
                is Result.Error -> {
                    _thisMonthSubCategoryGroup.postValue(ExpenseBySubCategoryResponse.error(result.error))
                }
            }
        }
    }

}