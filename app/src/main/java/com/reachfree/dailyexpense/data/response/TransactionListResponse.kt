package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class TransactionListResponse(
    val status: Status,
    val data: List<TransactionEntity>?,
    val error: String?
) {

    companion object {
        fun loading(): TransactionListResponse = TransactionListResponse(Status.LOADING, null, null)

        fun success(transaction: List<TransactionEntity>): TransactionListResponse =
            TransactionListResponse(Status.SUCCESS, transaction, null)

        fun error(error: String): TransactionListResponse =
            TransactionListResponse(Status.ERROR, null, error)
    }

}