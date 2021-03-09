package com.reachfree.dailyexpense.util

import com.reachfree.dailyexpense.data.model.Currency
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-09
 * Time: 오후 2:39
 */
object CurrencyUtils {

    private fun insertCommaWithSecondDigit(amount: BigDecimal): String {
        return DecimalFormat("###,##0.00").format(amount)
    }

    private fun insertComma(amount: BigDecimal): String {
        return DecimalFormat("#,###").format(amount)
    }

    fun changeAmountByCurrency(amount: BigDecimal): String {
        val currencySymbol = Constants.currentCurrency.symbol
        return when (Constants.currentCurrency.decimalPoint) {
            0 -> {
                val amountString = insertComma(amount)
                if (currencySymbol == Currency.KRW.symbol) {
                    "${amountString}원"
                } else {
                    "$currencySymbol $amountString"
                }
            }
            else -> {
                val amountString = insertCommaWithSecondDigit(amount)
                "$currencySymbol $amountString"
            }
        }
    }

    fun edtAmountTextWithSecondDigit(amount: Any): String {
        return DecimalFormat("#,##0.00").format(amount)
    }

    fun edtAmountTextWithZeroDigit(amount: Any): String {
        return DecimalFormat("#,###").format(amount)
    }

}