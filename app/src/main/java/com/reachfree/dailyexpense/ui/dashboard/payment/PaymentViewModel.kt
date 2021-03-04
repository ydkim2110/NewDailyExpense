package com.reachfree.dailyexpense.ui.dashboard.payment

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.data.repository.TransactionRepository
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountChartModel
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-04
 * Time: 오후 12:41
 */
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {


    val allTransactionByPayment = MediatorLiveData<List<PaymentChartModel>>()

    fun getAllTransactionByPaymentLiveData(
        startDate: Long,
        endDate: Long
    ) {
        val transactionByPattern = repository.getAllTransactionByPaymentLiveData(startDate, endDate)

        allTransactionByPayment.addSource(transactionByPattern) { result ->
            allTransactionByPayment.value = result
        }
    }

    val transactionListByPayment = MediatorLiveData<List<TransactionEntity>>()

    fun getTransactionSortedBy(
        sortType: Constants.SortType,
        startDate: Long,
        endDate: Long,
        type: IntArray,
        payment: IntArray
    ) {
        val paymentByAmount = repository.getTransactionByPaymentSortedByAmount(startDate, endDate, type, payment)
        val paymentByDate = repository.getTransactionByPaymentSortedByDate(startDate, endDate, type, payment)
        val paymentByCategory = repository.getTransactionByPaymentSortedByCategory(startDate, endDate, type, payment)

        transactionListByPayment.addSource(paymentByAmount) { result ->
            if (sortType == Constants.SortType.AMOUNT) {
                result?.let { transactionListByPayment.value = it }
            }
        }
        transactionListByPayment.addSource(paymentByDate) { result ->
            if (sortType == Constants.SortType.DATE) {
                result?.let { transactionListByPayment.value = it }
            }
        }
        transactionListByPayment.addSource(paymentByCategory) { result ->
            if (sortType == Constants.SortType.CATEGORY) {
                result?.let { transactionListByPayment.value = it }
            }
        }

        when (sortType) {
            Constants.SortType.AMOUNT -> paymentByAmount.value?.let { transactionListByPayment.value = it }
            Constants.SortType.DATE -> paymentByDate.value?.let { transactionListByPayment.value = it }
            Constants.SortType.CATEGORY -> paymentByCategory.value?.let { transactionListByPayment.value = it }
        }
    }

}