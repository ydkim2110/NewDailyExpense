package com.reachfree.dailyexpense.util

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.kizitonwose.calendarview.utils.yearMonth
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.SubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import javax.annotation.meta.When
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오후 8:07
 */
object AppUtils {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val defaultDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val yearDashMonthDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val addTransactionDateFormat = SimpleDateFormat("yyyy-MM-dd, EEE", Locale.getDefault())
    val yearMonthDateFormat = SimpleDateFormat("yyyy MMMM", Locale.getDefault())
    val monthDayDateFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
    val dayDateFormat = SimpleDateFormat("dd")
    val dayOfWeekDateFormat = SimpleDateFormat("EE")
    val monthDateFormat = SimpleDateFormat("MM")
    val monthYearDateFormat = SimpleDateFormat("MMMM yyyy")


    fun startOfMonth(month: YearMonth): Long {
        return month.atDay(1).toMillis()
    }

    fun endOfMonth(month: YearMonth): Long {
        return LocalDateTime.of(month.atEndOfMonth(), LocalTime.MAX).toMillis()!!
    }

    fun startOfBeforeFiveMonth(month: YearMonth): Long {
        return month.minusMonths(4).atDay(1).toMillis()
    }

    fun convertDateToYearMonth(date: Date): YearMonth {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().yearMonth
    }

    fun convertDateToLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun convertDateToCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    fun calculateStartOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
    }

    fun calculateEndOfDay(localDate: LocalDate): LocalDateTime {
        return LocalDateTime.of(localDate, LocalTime.MAX)
    }

    fun stringToDate(date: String): Date {
        val tempDate: Date
        tempDate = try {
            defaultDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            Date()
        }
        return tempDate
    }

    fun validated(vararg views: View): Boolean {
        var ok = true
        for (v in views) {
            if (v is EditText) {
                if (TextUtils.isEmpty(v.text.toString())) {
                    ok = false
                    v.error = v.resources.getString(R.string.this_field_is_required)
                }
            }
        }
        return ok
    }

    fun divideBigDecimal(numerator: BigDecimal, denominator: BigDecimal): BigDecimal {
        return numerator.divide(denominator, 0, BigDecimal.ROUND_HALF_UP)
    }

    fun calculatePercentage(numerator: BigDecimal, denominator: BigDecimal): Int {
        return numerator.divide(denominator, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal(100)).toInt()
    }

    fun insertComma(amount: BigDecimal): String {
        return DecimalFormat("#,###").format(amount)
    }

    fun changeAmountByCurrency(amount: BigDecimal): String {
        val amountString = insertComma(amount)
        val currencyUnit = Constants.currencySymbol
        return "$currencyUnit $amountString"
    }

    private fun changeNumberToComma(number: Int): String {
        return NumberFormat.getNumberInstance().format(number)
    }

    fun scaleIcon(context: Context, icon: Drawable, scaleFactor: Float): Drawable {
        val bitmap = icon.toBitmap()
        val sizeX = (icon.intrinsicWidth * scaleFactor).roundToInt()
        val sizeY = (icon.intrinsicHeight * scaleFactor).roundToInt()
        val bitmapResized = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false)
        return BitmapDrawable(context.resources, bitmapResized)
    }

    fun animateTextViewAmount(
        view: TextView,
        duration: Long,
        initialValue: Int,
        finalValue: Int
    ) {
        ValueAnimator.ofInt(initialValue, finalValue).apply {
            setDuration(duration)
            addUpdateListener {
                view.text = StringBuilder(Constants.currencySymbol)
                    .append(" ")
                    .append(changeNumberToComma(it.animatedValue as Int))
            }
            start()
        }
    }

    fun animateTextViewPercent(
        view: TextView,
        duration: Long,
        initialValue: Int,
        finalValue: Int
    ) {
        ValueAnimator.ofInt(initialValue, finalValue).apply {
            setDuration(duration)
            addUpdateListener {
                view.text = StringBuilder(changeNumberToComma(it.animatedValue as Int))
                    .append(Constants.percent)
            }
            start()
        }
    }


    fun getExpenseCategory(categoryId: String): Category {
        return Constants.expenseCategories().first { category -> category.id == categoryId }
    }

    fun getExpenseSubCategory(subCategoryId: String): SubCategory {
        return Constants.expenseSubCategories().first { subCategory -> subCategory.id == subCategoryId }
    }

    fun getIncomeCategory(categoryId: String): Category {
        return Constants.incomeCategories().first { category -> category.id == categoryId }
    }

    fun getFromToWeekday(startDate: Long, endDate: Long): StringBuilder {
        return StringBuilder("(")
            .append(monthDayDateFormat.format(Date(startDate)))
            .append(" ~ ")
            .append(monthDayDateFormat.format(Date(endDate)))
            .append(")")
    }

    fun animateProgressbar(progressBar: ProgressBar, percentage: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", 0, percentage).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    fun groupingTransactionByDate(data: List<TransactionEntity>?): HashMap<String, ArrayList<TransactionEntity>> {
        val groupedTransaction: HashMap<String, ArrayList<TransactionEntity>> = HashMap()

        data?.let { transactions ->
            for (i in transactions.indices) {
                val key = defaultDateFormat.format(transactions[i].registerDate)
                if (groupedTransaction.containsKey(key)) {
                    groupedTransaction[key]?.add(transactions[i])
                } else {
                    val list = ArrayList<TransactionEntity>()
                    list.add(transactions[i])
                    groupedTransaction[key] = list
                }
            }
        }
        return groupedTransaction
    }

    fun groupingTransactionBySubCategory(data: List<TransactionEntity>?): HashMap<String, ArrayList<TransactionEntity>> {
        val groupedTransaction: HashMap<String, ArrayList<TransactionEntity>> = HashMap()

        data?.let { transactions ->
            for (i in transactions.indices) {
                val key = transactions[i].subCategoryId
                if (groupedTransaction.containsKey(key)) {
                    groupedTransaction[key]?.add(transactions[i])
                } else {
                    val list = ArrayList<TransactionEntity>()
                    list.add(transactions[i])
                    groupedTransaction[key] = list
                }
            }
        }
        return groupedTransaction
    }

    fun decimalPoint(code: String) {
        when (code) {
            "USD" -> {  }
            else -> {}
        }

    }
}
