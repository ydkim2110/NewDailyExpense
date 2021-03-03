package com.reachfree.dailyexpense.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.TransactionFragmentBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

@AndroidEntryPoint
class TransactionFragment : BaseFragment<TransactionFragmentBinding>() {

    private val viewModel: TransactionViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TransactionFragmentBinding {
        return TransactionFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendarView()

        binding.fabAdd.setOnClickListener {
            showAddTransactionFragment()
        }
    }

    private fun showAddTransactionFragment() {
        val addTransactionFragment = AddExpenseFragment.newInstance()
        addTransactionFragment.show(childFragmentManager, null)
    }

    private fun setupCalendarView() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textViewCalendarDay = view.findViewById<TextView>(R.id.textViewCalendarDay)
            val imageViewIndicator = view.findViewById<ImageView>(R.id.imageViewIndicator)
            val viewExpenseDotIndicator = view.findViewById<View>(R.id.viewExpenseDotIndicator)
            val viewIncomeDotIndicator = view.findViewById<View>(R.id.viewIncomeDotIndicator)
        }

        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textViewCalendarDay.text = day.date.dayOfMonth.toString()
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
        binding.calendarView.apply {
            maxRowCount = 1
            hasBoundaries = false
        }
    }
}