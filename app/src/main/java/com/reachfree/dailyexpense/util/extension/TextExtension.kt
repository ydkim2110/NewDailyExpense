package com.reachfree.dailyexpense.util.extension

import android.animation.ValueAnimator
import android.content.Context
import android.widget.TextView
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.CurrencyUtils
import java.math.BigDecimal
import java.text.NumberFormat

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-09
 * Time: 오후 5:29
 */
fun Context.getQuantityString(
    quantity: Int,
    pluralResId: Int,
    zeroResId: Int? = null
): String  {
    return if (zeroResId != null && quantity == 0) {
        resources.getString(zeroResId)
    } else {
        resources.getQuantityString(pluralResId, quantity, quantity)
    }
}


fun TextView.animateAmount(
    finalValue: BigDecimal
) {
    ValueAnimator.ofInt(0, finalValue.toInt()).apply {
        duration = 1000L
        addUpdateListener {
            this@animateAmount.text =
                CurrencyUtils.changeAmountByCurrency(BigDecimal(it.animatedValue as Int))
        }
        start()
    }
}

fun TextView.animatePercent(
    finalValue: Int
) {
    ValueAnimator.ofInt(0, finalValue).apply {
        duration = 1000L
        addUpdateListener {
            this@animatePercent.text =
                StringBuilder(NumberFormat.getNumberInstance().format(it.animatedValue as Int))
                    .append(Constants.percent)
        }
        start()
    }
}
