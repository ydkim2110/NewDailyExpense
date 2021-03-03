package com.reachfree.dailyexpense.ui.budget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.reachfree.dailyexpense.R

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-23
 * Time: 오전 10:00
 */
class CustomBudgetDialog(context: Context) : Dialog(context) {

    init {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable())
        window?.setDimAmount(0.0f)
        setContentView(R.layout.custom_budget_dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    class Builder(context: Context) {
        private val dialog = CustomBudgetDialog(context)
        fun setTitle(text: String): Builder {
            dialog.setTitle(text)
            return this
        }
    }
}