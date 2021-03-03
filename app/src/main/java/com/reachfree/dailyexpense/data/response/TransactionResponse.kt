package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class TransactionResponse(
    val status: Status,
    val data: TransactionEntity?,
    val error: String?
) {

    companion object {
        fun loading(): TransactionResponse = TransactionResponse(Status.LOADING, null, null)

        fun success(transaction: TransactionEntity): TransactionResponse =
            TransactionResponse(Status.SUCCESS, transaction, null)

        fun error(error: String): TransactionResponse =
            TransactionResponse(Status.ERROR, null, error)
    }

}