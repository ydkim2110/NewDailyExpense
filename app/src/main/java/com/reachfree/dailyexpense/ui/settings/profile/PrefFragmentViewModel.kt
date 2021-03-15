package com.reachfree.dailyexpense.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-15
 * Time: 오전 10:17
 */
@HiltViewModel
class PrefFragmentViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    fun deleteAll() {
        viewModelScope.launch(dispatchers.io) {
            repository.deleteAll()
        }
    }

}