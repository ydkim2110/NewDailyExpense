package com.reachfree.dailyexpense.ui.budget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.ExpenseBudgetActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.budget.create.CreateBudgetFragment
import com.reachfree.dailyexpense.ui.budget.detail.ExpenseBudgetDetailFragment
import com.reachfree.dailyexpense.ui.dashboard.DashboardActivity
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.CurrencyUtils.changeAmountByCurrency
import com.reachfree.dailyexpense.util.extension.animateProgressbar
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.time.YearMonth
import java.util.*

@AndroidEntryPoint
class ExpenseBudgetActivity :
    BaseActivity<ExpenseBudgetActivityBinding>({ ExpenseBudgetActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private val viewModel: ExpenseBudgetViewModel by viewModels()
    private lateinit var expenseBudgetAdapter: ExpenseBudgetAdapter

    private var currentDate = Date()

    private var daysOfMonth = 1
    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideContentLayout()

        binding.noItemLayout.txtNoItem.text = getString(R.string.text_no_budget)
        binding.noItemLayout.imgNoItem.load(R.drawable.logo)

        val startOfMonth = AppUtils.startOfMonth(YearMonth.now())
        val endOfMonth = AppUtils.endOfMonth(YearMonth.now())
        viewModel.date.value = listOf(startOfMonth, endOfMonth)

        daysOfMonth = YearMonth.now().lengthOfMonth()

        setCurrentDate(Date())
        setupToolbar()
        setupRecyclerView()
        setupCalendarView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setCurrentDate(date: Date) {
        binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(date)
        binding.appBar.compactcalendarView.setCurrentDate(date)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.appBar.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        binding.recyclerBudget.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ExpenseBudgetActivity)
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
                binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(dateClicked!!)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                if (binding.noItemLayout.noItemLayout.isVisible) {
                    binding.noItemLayout.noItemLayout.isVisible = false
                }
                hideContentLayout()
                binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth)
                currentDate = firstDayOfNewMonth

                val dateTypeYearMonth = AppUtils.convertDateToYearMonth(firstDayOfNewMonth)
                val startOfMonth = AppUtils.startOfMonth(dateTypeYearMonth)
                val endOfMonth = AppUtils.endOfMonth(dateTypeYearMonth)

                daysOfMonth = dateTypeYearMonth.lengthOfMonth()

                viewModel.date.value = listOf(startOfMonth, endOfMonth)
            }
        })
    }

    private fun setupViewHandler() {
        binding.appBar.datePickerButton.setOnClickListener {
            val rotation = if (isExpanded) 0f else 180f
            ViewCompat.animate(binding.appBar.datePickerArrow).rotation(rotation).start()
            isExpanded = !isExpanded
            binding.appBar.appBar.setExpanded(isExpanded, true)
        }

        binding.expenseBudgetSummaryLayout.btnCreateBudget.setOnSingleClickListener {
            CreateBudgetFragment.newInstance().apply {
                show(supportFragmentManager, CreateBudgetFragment.TAG)
            }
        }
    }

    private fun subscribeToObserver() {
        viewModel.expenseBudgetList.observe(this) { data ->
            val budgetedAmount = data.sumOf { it.budgetAmount!! }
            val spentAmount = data.sumOf { it.sumByCategory!! }
            val leftAmount = budgetedAmount.minus(spentAmount)
            var leftBudgetPercent = 0

            if (budgetedAmount > BigDecimal(0)) {
                leftBudgetPercent = AppUtils.calculatePercentage(spentAmount, budgetedAmount)

                AppUtils.animateTextViewPercent(
                    binding.expenseBudgetSummaryLayout.txtLeftToSpendPercent,
                    DashboardActivity.ANIMATION_DURATION,
                    0,
                    leftBudgetPercent
                )
            } else if (budgetedAmount == BigDecimal(0)) {
                AppUtils.animateTextViewPercent(
                    binding.expenseBudgetSummaryLayout.txtLeftToSpendPercent,
                    DashboardActivity.ANIMATION_DURATION,
                    0,
                    0
                )
            }

            with(binding.expenseBudgetSummaryLayout) {
                txtLeftToSpendAmount.text = resources.getString(R.string.text_budget_left, changeAmountByCurrency(leftAmount))
                txtBudgetedAmount.text = resources.getString(R.string.text_budget_out_of_budgeted, changeAmountByCurrency(budgetedAmount))

                when {
                    budgetedAmount == BigDecimal(0) -> {
                        txtBudgetComment.text = ""
                    }
                    spentAmount > budgetedAmount -> {
                        txtBudgetComment.text = resources.getString(R.string.text_budget_you_spent_this_month_exceed, changeAmountByCurrency(spentAmount))
                    }
                    else -> {
                        txtBudgetComment.text = resources.getString(R.string.text_budget_you_spent_this_month, changeAmountByCurrency(spentAmount))
                    }
                }

                progressbarBudget.animateProgressbar(leftBudgetPercent)
            }
            expenseBudgetAdapter = ExpenseBudgetAdapter(daysOfMonth)
            binding.recyclerBudget.adapter = expenseBudgetAdapter
            expenseBudgetAdapter.submitList(data.sortedByDescending { it.sumByCategory })

            expenseBudgetAdapter.setOnItemClickListener { categoryId ->
                showDetailFragment(categoryId)
            }

            if (data.isEmpty()) {
                if (binding.noItemLayout.noItemLayout.isGone) {
                    binding.noItemLayout.noItemLayout.isVisible = true
                    binding.contentLayout.visibility = View.VISIBLE
                    binding.progressBarWaiting.visibility = View.GONE
                }
            } else {
                if (binding.noItemLayout.noItemLayout.isVisible) {
                    binding.noItemLayout.noItemLayout.isVisible = false
                }
                showContentLayout()
            }
        }
    }

    private fun showDetailFragment(categoryId: String) {
        ExpenseBudgetDetailFragment.newInstance(categoryId, currentDate.time).apply {
            show(supportFragmentManager, ExpenseBudgetDetailFragment.TAG)
        }
    }

    private fun hideContentLayout() {
        binding.contentLayout.visibility = View.GONE
        binding.progressBarWaiting.visibility = View.VISIBLE
    }

    private fun showContentLayout() {
        binding.contentLayout.visibility = View.VISIBLE
        binding.progressBarWaiting.visibility = View.GONE
    }

    companion object {
        fun start(context: Context) {
            Intent(context, ExpenseBudgetActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}
