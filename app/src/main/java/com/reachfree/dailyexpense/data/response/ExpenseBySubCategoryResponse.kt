package com.reachfree.dailyexpense.data.response

import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.util.Constants.Status

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오전 9:44
 */
class ExpenseBySubCategoryResponse(
    val status: Status,
    val data: List<ExpenseBySubCategory>?,
    val error: String?
) {

    companion object {
        fun loading(): ExpenseBySubCategoryResponse = ExpenseBySubCategoryResponse(Status.LOADING, null, null)

        fun success(transaction: List<ExpenseBySubCategory>): ExpenseBySubCategoryResponse =
            ExpenseBySubCategoryResponse(Status.SUCCESS, transaction, null)

        fun error(error: String): ExpenseBySubCategoryResponse =
            ExpenseBySubCategoryResponse(Status.ERROR, null, error)
    }

}