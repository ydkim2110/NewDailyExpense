package com.reachfree.dailyexpense.ui.transaction

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.TransactionActivityBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.viewmodel.TransactionViewModel
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.AppUtils.calculatePercentage
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.SortType
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.extension.animateProgressbar
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.time.*
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TransactionActivity : BaseActivity<TransactionActivityBinding>({ TransactionActivityBinding.inflate(it) }),
    TGListHeaderAdapter.OnItemClickListener {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TGListHeaderAdapter

    private var currentDate = ""
    private var isExpanded = false

    private var startOfMonth = YearMonth.now().atDay(1).toMillis()
    private var endOfMonth = YearMonth.now().atEndOfMonth().toMillis()

    private var isOptionsExpanded = false
    private var selectedSortIndex = 0
    private var selectedTransactionIndex = 0
    private var beforeSelectedTransactionIndex = 0
    private lateinit var filterSortArray: Array<String>
    private lateinit var filterTransactionArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filterSortArray = resources.getStringArray(R.array.filter_sort_options)
        filterTransactionArray = resources.getStringArray(R.array.filter_transaction_options)

        binding.noItemLayout.txtNoItem.text = getString(R.string.text_no_transaction)
        binding.noItemLayout.imgNoItem.load(R.drawable.logo)

        setupToolbar()
        setupCalendarView()
        setupOptions()
        subscribeToObserver()

        viewModel.getTransactionSortedBy(
            SortType.AMOUNT,
            startOfMonth,
            endOfMonth,
            intArrayOf(EXPENSE.ordinal, INCOME.ordinal)
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.appBar.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(Date())
        binding.appBar.compactcalendarView.setCurrentDate(Date())

        binding.appBar.datePickerButton.setOnClickListener {
            val rotation = if (isExpanded) 0f else 180f
            ViewCompat.animate(binding.appBar.datePickerArrow).rotation(rotation).start()
            isExpanded = !isExpanded
            binding.appBar.appBar.setExpanded(isExpanded, true)
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
                if (binding.noItemLayout.noItemLayout.isVisible) {
                    binding.noItemLayout.noItemLayout.isVisible = false
                }
                hideContentLayout()

                binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth)
                setupNewCalendarView(firstDayOfNewMonth)
            }
        })
    }

    private fun setupNewCalendarView(firstDayOfNewMonth: Date) {
        currentDate = AppUtils.defaultDateFormat.format(firstDayOfNewMonth)
        startOfMonth = AppUtils.startOfMonth(AppUtils.convertDateToYearMonth(firstDayOfNewMonth))
        endOfMonth = AppUtils.endOfMonth(AppUtils.convertDateToYearMonth(firstDayOfNewMonth))

        when (beforeSelectedTransactionIndex) {
            0 -> {
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal)
                )
            }
            1 -> {
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INCOME.ordinal)
                )
            }
            else -> {
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal, INCOME.ordinal)
                )
            }
        }
    }

    private fun setupOptions() {
        binding.relativeLayoutOptionsFragment.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.txtSwitcherSortBy.setFactory {
            val textView = TextView(this)

            textView.apply {
                setTextColor(ContextCompat.getColor(this@TransactionActivity, R.color.colorTextPrimary))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
//                typeface = ResourcesCompat.getFont(requireContext(), R.font.cabin_bold)
            }

            textView
        }

        binding.txtSwitcherSortBy.setText(filterSortArray[selectedSortIndex])

        binding.txtSwitcherSortBy.setOnClickListener {
            when {
                selectedSortIndex > 1 -> {
                    selectedSortIndex = 0
                    updateBySort(SortType.AMOUNT)
                }
                selectedSortIndex > 0 -> {
                    selectedSortIndex ++
                    updateBySort(SortType.CATEGORY)
                }
                else -> {
                    selectedSortIndex ++
                    updateBySort(SortType.DATE)
                }
            }

            binding.txtSwitcherSortBy.setText(filterSortArray[selectedSortIndex])
        }

        binding.txtSwitcherTransaction.setFactory {
            val textView = TextView(this)

            textView.apply {
                setTextColor(ContextCompat.getColor(this@TransactionActivity, R.color.colorTextPrimary))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
//                typeface = ResourcesCompat.getFont(requireContext(), R.font.cabin_bold)
            }

            textView
        }

        binding.txtSwitcherTransaction.setText(filterTransactionArray[selectedTransactionIndex])

        binding.txtSwitcherTransaction.setOnClickListener {
            selectedSortIndex = 0
            binding.txtSwitcherSortBy.setText(filterSortArray[selectedSortIndex])

            updateByTransaction()

            binding.txtSwitcherTransaction.setText(filterTransactionArray[selectedTransactionIndex])

        }

        binding.btnOptions.setOnClickListener {
            isOptionsExpanded = if (isOptionsExpanded) {
                ObjectAnimator.ofFloat(binding.imageViewOptions,
                    ROTATION, 0f)
                    .setDuration(ROTATION_ANIM_DURATION)
                    .start()
                binding.linearLayoutOptions.visibility = View.GONE
                false
            } else {
                ObjectAnimator.ofFloat(binding.imageViewOptions,
                    ROTATION, 180f)
                    .setDuration(ROTATION_ANIM_DURATION)
                    .start()
                binding.linearLayoutOptions.visibility = View.VISIBLE
                true
            }
        }
    }

    private fun updateBySort(sortType: SortType) {
        when (beforeSelectedTransactionIndex) {
            0 -> {
                viewModel.getTransactionSortedBy(
                    sortType,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal)
                )
            }
            1 -> {
                viewModel.getTransactionSortedBy(
                    sortType,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INCOME.ordinal)
                )
            }
            else -> {
                viewModel.getTransactionSortedBy(
                    sortType,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal, INCOME.ordinal)
                )
            }
        }
    }

    private fun updateByTransaction() {
        when {
            selectedTransactionIndex > 1 -> {
                beforeSelectedTransactionIndex = selectedTransactionIndex
                selectedTransactionIndex = 0
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal, INCOME.ordinal)
                )
            }
            selectedTransactionIndex > 0 -> {
                beforeSelectedTransactionIndex = selectedTransactionIndex
                selectedTransactionIndex ++
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INCOME.ordinal)
                )
            }
            else -> {
                beforeSelectedTransactionIndex = selectedTransactionIndex
                selectedTransactionIndex ++
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal)
                )
            }
        }
    }

    private fun subscribeToObserver() {
        viewModel.subCategoryList.observe(this) { data ->
            setupTransactionSummaryLayout(data)
            setupIncomeExpenseLayout(data)
            setupRecyclerView()

            val groupTransactions = AppUtils.groupingTransactionByDate(data)

            val keyList = ArrayList(groupTransactions.keys)
            val valueList = ArrayList(groupTransactions.values)

            val newList = ArrayList<TransactionGroup>()
            if (keyList.size == valueList.size) {
                for (i in keyList.indices) {
                    val transactionGroup = TransactionGroup().apply {
                        key = keyList[i]
                        transactionList = valueList[i]
                    }
                    newList.add(transactionGroup)
                }
            }
            newList.sortByDescending { it.key }
            transactionAdapter = TGListHeaderAdapter(this)
            binding.recyclerTransaction.adapter = transactionAdapter
            transactionAdapter.submitList(newList)

            if (newList.isEmpty()) {
                if (binding.noItemLayout.noItemLayout.isGone) {
                    binding.noItemLayout.noItemLayout.isVisible = true
                    binding.contentLayout.visibility = View.GONE
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

    override fun onItemClickListener(transaction: TransactionEntity) {
        when (transaction.type) {
            EXPENSE.ordinal -> {
                val addExpenseFragment = AddExpenseFragment.newInstance(transaction)
                addExpenseFragment.show(supportFragmentManager, null)
            }
            INCOME.ordinal -> {
                val addIncomeFragment = AddIncomeFragment.newInstance(transaction)
                addIncomeFragment.show(supportFragmentManager, null)
            }
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

    private fun setupTransactionSummaryLayout(data: List<TransactionEntity>?) {
        data?.let { transactions ->
            val expenses = transactions
                .filter { it.type == EXPENSE.ordinal }
                .sumOf { it.amount!! }

            AppUtils.animateTextViewAmount(
                binding.layoutWeekSummary.txtMonthlySumAmount,
                ANIMATION_DURATION,
                START_VALUE,
                expenses
            )

            val normalExpense = data
                .filter { it.type == EXPENSE.ordinal }
                .filter { it.pattern == NORMAL.ordinal }
                .sumOf { it.amount!! }
            val wasteExpense = data
                .filter { it.type == EXPENSE.ordinal }
                .filter { it.pattern == WASTE.ordinal }
                .sumOf { it.amount!! }
            val investExpense = data
                .filter { it.type == EXPENSE.ordinal }
                .filter { it.pattern == INVEST.ordinal }
                .sumOf { it.amount!! }

            AppUtils.animateTextViewAmount(
                binding.layoutWeekSummary.txtNormalAmount,
                ANIMATION_DURATION,
                0,
                normalExpense
            )
            AppUtils.animateTextViewAmount(
                binding.layoutWeekSummary.txtWasteAmount,
                ANIMATION_DURATION,
                0,
                wasteExpense
            )
            AppUtils.animateTextViewAmount(
                binding.layoutWeekSummary.txtInvestAmount,
                ANIMATION_DURATION,
                0,
                investExpense
            )

            setupProgressbar(normalExpense, wasteExpense, investExpense)
        }
    }

    private fun setupIncomeExpenseLayout(data: List<TransactionEntity>?) {
        data?.let { transactions ->
            val expenses = transactions
                .filter { it.type == EXPENSE.ordinal }
                .sumOf { it.amount!! }

            val incomes = transactions
                .filter { it.type == INCOME.ordinal }
                .sumOf { it.amount!! }

            AppUtils.animateTextViewAmount(
                binding.incomeExpenseLayout.txtIncomeAmount,
                ANIMATION_DURATION,
                0,
                incomes
            )
            AppUtils.animateTextViewAmount(
                binding.incomeExpenseLayout.txtExpenseAmount,
                ANIMATION_DURATION,
                0,
                expenses
            )
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TransactionActivity)
            addItemDecoration(DividerItemDecoration(this@TransactionActivity, LinearLayoutManager.VERTICAL))
            if (this.itemDecorationCount > 0) {
                this.removeItemDecorationAt(0)
            }
        }
    }

    private fun setupProgressbar(
        normalExpense: BigDecimal,
        wasteExpense: BigDecimal,
        investExpense: BigDecimal
    ) {
        var normalPercent = 0
        var wastePercent = 0
        var investPercent = 0
        val totalExpense = normalExpense.add(wasteExpense).add(investExpense)

        if (totalExpense > BigDecimal(0)) {
            normalPercent = calculatePercentage(normalExpense, totalExpense)
            wastePercent = calculatePercentage(wasteExpense, totalExpense)
            investPercent = calculatePercentage(investExpense, totalExpense)
        }

        binding.layoutWeekSummary.normalProgressbar.animateProgressbar(normalPercent)
        binding.layoutWeekSummary.wasteProgressbar.animateProgressbar(wastePercent)
        binding.layoutWeekSummary.investProgressbar.animateProgressbar(investPercent)
    }

    companion object {
        private const val ROTATION = "rotation"
        private const val ROTATION_ANIM_DURATION = 250L
        const val ANIMATION_DURATION = 1000L
        const val START_VALUE = 0

        fun start(context: Context) {
            Intent(context, TransactionActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}