package com.reachfree.dailyexpense.ui.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.dailyexpense.data.Result
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.data.response.TransactionResponse
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오후 11:44
 */
@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var _transaction = MutableLiveData<TransactionResponse>()
    val transaction get() = _transaction

    fun getTransactionById(id: Int) {
        viewModelScope.launch(dispatchers.io) {
            _transaction.postValue(TransactionResponse.loading())
            when (val result = repository.getTransactionById(id)) {
                is Result.Success -> {
                    _transaction.postValue(TransactionResponse.success(result.data))
                }
                is Result.Error -> {
                    _transaction.postValue(TransactionResponse.error(result.error))
                }
            }
        }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                repository.insert(transaction)
            } catch (e: Exception) {
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                repository.update(transaction)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                repository.delete(transaction)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTransactionById(id: Int) {
        viewModelScope.launch(dispatchers.io) {
            try {
                repository.deleteById(id)
            } catch (e: Exception) {
            }
        }
    }

}