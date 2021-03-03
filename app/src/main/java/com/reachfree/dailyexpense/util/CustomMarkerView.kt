package com.reachfree.dailyexpense.util

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import timber.log.Timber

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-18
 * Time: 오후 12:49
 */
class CustomMarkerView(
    val list: List<ExpenseByCategory>,
    context: Context,
    val layoutResId: Int
) : MarkerView(context, layoutResId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        val txtCount = findViewById<TextView>(R.id.txtCount)
        val txtAmount = findViewById<TextView>(R.id.txtAmount)

        if (e == null) {
            return
        }

        Timber.d("e.y: ${e}")
        Timber.d("e.x: ${e.y.toInt()}")
        val currentExpenseByCategoryId = e.x.toInt()
        val expenseByCategory = list[currentExpenseByCategoryId]

        txtCount.text = expenseByCategory.countByCategory.toString()
        txtAmount.text = e.y.toString()
    }

}