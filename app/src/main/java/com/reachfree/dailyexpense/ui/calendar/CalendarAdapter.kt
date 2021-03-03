package com.reachfree.dailyexpense.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemCalendarDayBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import timber.log.Timber
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-19
 * Time: 오후 8:34
 */
class CalendarAdapter(
    private val transactionList: List<TransactionEntity>,
    private val dateList: List<Date>,
    private val calendar: Calendar,
    private val recyclerItemHeight: Int
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(date: Date)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class MyViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date

            val dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH)
            val displayYear = dateCalendar.get(Calendar.YEAR)
            val displayMonth = dateCalendar.get(Calendar.MONTH) + 1
            val displayDay = dateCalendar.get(Calendar.DAY_OF_MONTH)

            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val todayYear = Calendar.getInstance().get(Calendar.YEAR)
            val todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
            val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            with(binding) {
                if (bindingAdapterPosition % 7 == 0) {
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_red_dark))
                } else if (bindingAdapterPosition % 7 == 6) {
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_blue_dark))
                }

                if (!(displayMonth == currentMonth && displayYear == currentYear)) {
                    txtCalendarDay.visibility = View.GONE
                    root.isClickable = false
                } else {
                    root.setOnSingleClickListener {
                        onItemClickListener?.onItemClick(date)
                    }
                }

                if (displayYear == todayYear && displayMonth == todayMonth && displayDay == todayDay) {
                    txtCalendarDay.setBackgroundResource(R.drawable.bg_calendar_day)
                    txtCalendarDay.setTextColor(ContextCompat.getColor(root.context, android.R.color.white))
                }

                txtCalendarDay.text = dayNo.toString()

                val eventCalendar = Calendar.getInstance()
                val expenseTotal = ArrayList<BigDecimal>()
                val incomeTotal = ArrayList<BigDecimal>()
                for (i in transactionList.indices) {
                    eventCalendar.time = Date(transactionList[i].registerDate)
                    if (dayNo == eventCalendar.get(Calendar.DAY_OF_MONTH)
                        && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                        && displayYear == eventCalendar.get(Calendar.YEAR)) {

                        if (transactionList[i].type == Constants.TYPE.EXPENSE.ordinal) {
                            expenseTotal.add(transactionList[i].amount!!)

                            txtExpenseAmount.text = AppUtils.insertComma(expenseTotal.sumOf { it })
                        } else if (transactionList[i].type == Constants.TYPE.INCOME.ordinal) {
                            incomeTotal.add(transactionList[i].amount!!)

                            txtIncomeAmount.text = AppUtils.insertComma(incomeTotal.sumOf { it })
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params: GridLayoutManager.LayoutParams = binding.root.layoutParams as GridLayoutManager.LayoutParams
        params.height = recyclerItemHeight / 5
        binding.root.layoutParams = params
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dateList[position])
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

}