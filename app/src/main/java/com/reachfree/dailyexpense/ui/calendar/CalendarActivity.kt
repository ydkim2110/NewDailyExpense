package com.reachfree.dailyexpense.ui.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.kizitonwose.calendarview.utils.yearMonth
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.CalendarActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.calendar.bottomsheet.TransactionListBottomSheet
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.SpacesItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CalendarActivity :
    BaseActivity<CalendarActivityBinding>({ CalendarActivityBinding.inflate(it)}),
    CalendarAdapter.OnItemClickListener {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter

    private lateinit var transactionListBottomSheet: TransactionListBottomSheet

    private var isExpanded = false

    private val dateList = ArrayList<Date>()
    private var calendar = Calendar.getInstance()

    private var recyclerItemHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCurrentDate(Date())
        calendar.time = Date()

        binding.appBar.datePickerButton.setOnClickListener {
            val rotation = if (isExpanded) 0f else 180f
            ViewCompat.animate(binding.appBar.datePickerArrow).rotation(rotation).start()
            isExpanded = !isExpanded
            binding.appBar.appBar.setExpanded(isExpanded, true)
        }

        binding.recyclerCalendar.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@CalendarActivity, SPAN_COUNT)
            addItemDecoration(SpacesItemDecoration(0))
        }

        setupToolbar()
        setupCalendar()
        setupCalendarView()

        val startOfMonth = AppUtils.startOfMonth(YearMonth.now())
        val endOfMonth = AppUtils.endOfMonth(YearMonth.now())

        viewModel.date.value = listOf(startOfMonth, endOfMonth)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        val display = windowManager.defaultDisplay
        val height = display.height
        val toolbarHeight = binding.appBar.toolbar.height
        val rectangle = Rect()
        val statusBarHeight = rectangle.top
        val dayHeight = binding.dayOfWeekLayout.height
        recyclerItemHeight = height - statusBarHeight - toolbarHeight - dayHeight

        subscribeToObserver()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.appBar.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupCalendar() {
        dateList.clear()
        val monthCalendar: Calendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (dateList.size < MAX_CALENDAR_DAY) {
            dateList.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun setupCalendarView() {
        binding.appBar.compactcalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault())
        binding.appBar.compactcalendarView.setShouldDrawDaysHeader(true)
        binding.appBar.compactcalendarView.setDayColumnNames(resources.getStringArray(R.array.day_of_week))
        binding.appBar.compactcalendarView.setFirstDayOfWeek(Constants.FIRST_DAY_OF_WEEK)
        binding.appBar.compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth)

                val dateTypeYearMonth = firstDayOfNewMonth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().yearMonth
                val startOfMonth = AppUtils.startOfMonth(dateTypeYearMonth)
                val endOfMonth = AppUtils.endOfMonth(dateTypeYearMonth)

                dateList.clear()
                calendar.time = firstDayOfNewMonth

                val monthCalendar: Calendar = calendar.clone() as Calendar
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
                val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
                monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

                while (dateList.size < MAX_CALENDAR_DAY) {
                    dateList.add(monthCalendar.time)
                    monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                viewModel.date.value = listOf(startOfMonth, endOfMonth)
            }
        })
    }

    private fun subscribeToObserver() {
        viewModel.transactionList.observe(this) { result ->
            calendarAdapter = CalendarAdapter(result, dateList, calendar, recyclerItemHeight)
            calendarAdapter.setOnItemClickListener(this)
            binding.recyclerCalendar.adapter = calendarAdapter
        }
    }

    private fun setCurrentDate(date: Date) {
        binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(date)
        binding.appBar.compactcalendarView.setCurrentDate(date)
    }

    override fun onItemClick(date: Date) {
        transactionListBottomSheet = TransactionListBottomSheet(date)
        transactionListBottomSheet.isCancelable = true
        transactionListBottomSheet.show(supportFragmentManager, TransactionListBottomSheet.TAG)
    }

    companion object {
        private const val MAX_CALENDAR_DAY = 35
        private const val SPAN_COUNT = 7

        fun start(context: Context) {
            Intent(context, CalendarActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}