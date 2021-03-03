package com.reachfree.dailyexpense.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.android.material.navigation.NavigationView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.DashboardActivityBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.bottomsheet.SelectTypeBottomSheet
import com.reachfree.dailyexpense.ui.budget.ExpenseBudgetActivity
import com.reachfree.dailyexpense.ui.calendar.CalendarActivity
import com.reachfree.dailyexpense.ui.dashboard.pattern.PatternDetailFragment
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountFragment
import com.reachfree.dailyexpense.ui.settings.PREF_APP_THEME
import com.reachfree.dailyexpense.ui.settings.PREF_CURRENCY
import com.reachfree.dailyexpense.ui.settings.SettingsActivity
import com.reachfree.dailyexpense.ui.transaction.TransactionActivity
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.AppUtils.animateProgressbar
import com.reachfree.dailyexpense.util.AppUtils.animateTextViewAmount
import com.reachfree.dailyexpense.util.AppUtils.animateTextViewPercent
import com.reachfree.dailyexpense.util.AppUtils.calculatePercentage
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.FIRST_DAY_OF_WEEK
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.PAYMENT
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.PreferenceHelper.get
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.runDelayed
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.time.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity :
    BaseActivity<DashboardActivityBinding>({ DashboardActivityBinding.inflate(it) }),
    NavigationView.OnNavigationItemSelectedListener,
    RecentTGListHeaderAdapter.OnItemClickListener {

    @Inject lateinit var sessionManager: SessionManager

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentTGListHeaderAdapter: RecentTGListHeaderAdapter

    private val selectTypeBottomSheet by lazy {
        SelectTypeBottomSheet()
    }

    private var doubleBackToExit = false
    private var isExpanded = false
    private var currentDate = Date()

    private var currentStartDate: Long = 0L
    private var currentEndDate: Long = 0L
    private var currentRecentStartDate: Long = 0L
    private var currentRecentEndDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.currencySymbol = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol

        val startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(1).toMillis()!!
        val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toMillis()!!
        currentRecentStartDate = startDate
        currentRecentEndDate = endDate

        val startOfMonth = AppUtils.startOfMonth(YearMonth.now())
        val endOfMonth = AppUtils.endOfMonth(YearMonth.now())
        currentStartDate = startOfMonth
        currentEndDate = endOfMonth

        viewModel.dateForMonthly.value = listOf(startOfMonth, endOfMonth)
        viewModel.dateForRecent.value = listOf(startDate,endDate)

        setCurrentDate(Date())

        binding.recentTransactionsLayout.txtNoItem.text = getString(R.string.text_no_transaction)
        binding.recentTransactionsLayout.imgNoItem.setImageResource(R.drawable.avatar)

        setupToolbar()
        setupNavigation()
        setupCalendarView()
        setupRecyclerView()
        setupViewHandler()
        setupSelectTypeListener()
        subscribeToObserver()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            if (PREF_CURRENCY == key) {
                Constants.currencySymbol = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol
                viewModel.dateForMonthly.value = listOf(currentStartDate, currentEndDate)
                viewModel.dateForRecent.value = listOf(currentRecentStartDate, currentRecentEndDate)
//                viewModel.getAllTransactionsByDate(currentStartDate, currentEndDate)
//                viewModel.getRecentTransactionsByDate(currentRecentStartDate, currentRecentEndDate)
            } else if (PREF_APP_THEME == key) {

            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_transaction -> {
                runAfterDrawerClose { TransactionActivity.start(this) }
            }
            R.id.nav_budget -> {
                runAfterDrawerClose { ExpenseBudgetActivity.start(this) }
            }
            R.id.nav_calendar -> {
                runAfterDrawerClose { CalendarActivity.start(this) }
            }
            R.id.nav_settings -> {
                runAfterDrawerClose { SettingsActivity.start(this) }
            }
        }
        return false
    }

    private fun runAfterDrawerClose(action: () -> Unit) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        runDelayed(DRAWER_CLOSE_DELAY) { action() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        val toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX: Float = drawerView.width * slideOffset / 2
                binding.contentLayout.translationX = slideX
            }
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupCalendarView() {
        binding.compactcalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault())
        binding.compactcalendarView.setShouldDrawDaysHeader(true)
        binding.compactcalendarView.setDayColumnNames(resources.getStringArray(R.array.day_of_week))
        binding.compactcalendarView.setFirstDayOfWeek(FIRST_DAY_OF_WEEK)
        binding.compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                binding.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                binding.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth)
                setupNewCalendarView(firstDayOfNewMonth)
            }
        })
    }

    private fun setupNewCalendarView(firstDayOfNewMonth: Date) {
        currentDate = firstDayOfNewMonth
        val dateTypeYearMonth = AppUtils.convertDateToYearMonth(firstDayOfNewMonth)
        val startOfMonth = AppUtils.startOfMonth(dateTypeYearMonth)
        val endOfMonth = AppUtils.endOfMonth(dateTypeYearMonth)
        currentStartDate = startOfMonth
        currentEndDate = endOfMonth

        viewModel.dateForMonthly.value = listOf(startOfMonth, endOfMonth)

        val dateCalendar = Calendar.getInstance()
        val displayYear = dateCalendar.get(Calendar.YEAR)
        val displayMonth = dateCalendar.get(Calendar.MONTH) + 1

        dateCalendar.time = firstDayOfNewMonth
        val currentYear = dateCalendar.get(Calendar.YEAR)
        val currentMonth = dateCalendar.get(Calendar.MONTH) + 1

        if (displayYear == currentYear && displayMonth == currentMonth) {
            val startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(1).toMillis()!!
            val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toMillis()!!
            currentRecentStartDate = startDate
            currentRecentEndDate = endDate

            viewModel.dateForRecent.value = listOf(startDate, endDate)
        }  else if (displayYear >= currentYear || displayMonth > currentMonth) {
            val startDate = AppUtils.startOfMonth(dateTypeYearMonth)
            val endDate = AppUtils.endOfMonth(dateTypeYearMonth)
            currentRecentStartDate = startDate
            currentRecentEndDate = endDate

            viewModel.dateForRecent.value = listOf(startDate, endDate)
        } else {
            val startDate = LocalDateTime.of(dateTypeYearMonth.atEndOfMonth().minusDays(6), LocalTime.MIDNIGHT).toMillis()!!
            val endDate = LocalDateTime.of(dateTypeYearMonth.atEndOfMonth(), LocalTime.MAX).toMillis()!!
            currentRecentStartDate = startDate
            currentRecentEndDate = endDate

            viewModel.dateForRecent.value = listOf(startDate, endDate)
        }
    }

    private fun setupRecyclerView() {
        binding.recentTransactionsLayout.recyclerRecentTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DashboardActivity)
        }
    }

    private fun subscribeToObserver() {
        viewModel.monthlyTransaction.observe(this) { result ->
            setupTotalAmountLayout(result)
            setupExpensePatternLayout(result)
            setupPaymentSummaryLayout(result)
        }

        viewModel.recentTransaction.observe(this) { result ->
            if (result.isEmpty()) {
                binding.recentTransactionsLayout.recyclerRecentTransaction.visibility = View.GONE
                binding.recentTransactionsLayout.noItemLayout.visibility = View.VISIBLE
            } else {
                binding.recentTransactionsLayout.recyclerRecentTransaction.visibility = View.VISIBLE
                binding.recentTransactionsLayout.noItemLayout.visibility = View.GONE
            }

            val groupTransactions = AppUtils.groupingTransactionByDate(result)

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

            recentTGListHeaderAdapter = RecentTGListHeaderAdapter(sessionManager, this)
            binding.recentTransactionsLayout.recyclerRecentTransaction.adapter = recentTGListHeaderAdapter
            recentTGListHeaderAdapter.submitList(newList)
        }
    }

    private fun setupViewHandler() {
        binding.datePickerButton.setOnSingleClickListener {
            val rotation = if (isExpanded) 0f else 180f
            ViewCompat.animate(binding.datePickerArrow).rotation(rotation).start()
            isExpanded = !isExpanded
            binding.appBar.setExpanded(isExpanded, true)
        }

        binding.addNewTransaction.setOnSingleClickListener {
            selectTypeBottomSheet.isCancelable = true
            selectTypeBottomSheet.show(supportFragmentManager, SelectTypeBottomSheet.TAG)
        }

        binding.totalAmountLayout.totalAmountLayout.setOnSingleClickListener {
            TotalAmountFragment.newInstance(currentDate.time).apply {
                show(supportFragmentManager, TotalAmountFragment.TAG)
            }
        }
        
        binding.expensePatternLayout.btnTotalList.setOnSingleClickListener {
            showPatternDetailFragment(Constants.EXPENSE_PATTERN_TOTAL)
        }
        binding.expensePatternLayout.progressbarNormal.setOnSingleClickListener {
            showPatternDetailFragment(Constants.EXPENSE_PATTERN_NORMAL)
        }
        binding.expensePatternLayout.progressbarWaste.setOnSingleClickListener {
            showPatternDetailFragment(Constants.EXPENSE_PATTERN_WASTE)
        }
        binding.expensePatternLayout.progressbarInvest.setOnSingleClickListener {
            showPatternDetailFragment(Constants.EXPENSE_PATTERN_INVEST)
        }
    }

    private fun showPatternDetailFragment(pattern: Int) {
        PatternDetailFragment.newInstance(pattern, currentDate.time).apply {
            show(supportFragmentManager, null)
        }
    }

    private fun setupSelectTypeListener() {
        selectTypeBottomSheet.setSelectTypeListener(object : SelectTypeBottomSheet.OnSelectTypeListener {
            override fun onSelected(isExpense: Boolean) {
                if (isExpense) {
                    AddExpenseFragment.newInstance().apply { show(supportFragmentManager, null) }
                } else {
                    AddIncomeFragment.newInstance().apply { show(supportFragmentManager, null) }
                }
            }
        })
    }

    private fun setupTotalAmountLayout(data: List<TransactionEntity>?) {
        data?.let {
            val expenses = data
                .filter { it.type == EXPENSE.ordinal }
                .sumOf { it.amount!! }

            val incomes = data
                .filter { it.type == INCOME.ordinal }
                .sumOf { it.amount!! }

            with(binding.totalAmountLayout) {
                animateTextViewAmount(txtTotalAmount, ANIMATION_DURATION, START_VALUE, (incomes - expenses).toInt())
                animateTextViewAmount(txtTotalIncome, ANIMATION_DURATION, START_VALUE, incomes.toInt())
                animateTextViewAmount(txtTotalExpense, ANIMATION_DURATION, START_VALUE, expenses.toInt())
            }

        }
    }

    private fun setupExpensePatternLayout(data: List<TransactionEntity>?) {
        data?.let {
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

            with(binding.expensePatternLayout) {
                animateTextViewAmount(txtNormalSum, ANIMATION_DURATION, START_VALUE, normalExpense.toInt())
                animateTextViewAmount(txtWasteSum, ANIMATION_DURATION, START_VALUE, wasteExpense.toInt())
                animateTextViewAmount(txtInvestSum, ANIMATION_DURATION, START_VALUE, investExpense.toInt())
            }


            setupProgressbar(normalExpense, wasteExpense, investExpense)
        }
    }


    private fun setupPaymentSummaryLayout(data: List<TransactionEntity>?) {
        data?.let { result ->
            val creditAmount = result
                .filter { it.type == EXPENSE.ordinal }
                .filter { it.payment == PAYMENT.CREDIT.ordinal }
                .sumOf { it.amount!! }
            val cashAmount = result
                .filter { it.type == EXPENSE.ordinal }
                .filter { it.payment == PAYMENT.CASH.ordinal }
                .sumOf { it.amount!! }

            with(binding.paymentSummaryLayout) {
                animateTextViewAmount(txtCreditAmount, ANIMATION_DURATION, START_VALUE, creditAmount.toInt())
                animateTextViewAmount(txtCashAmount, ANIMATION_DURATION, START_VALUE, cashAmount.toInt())
            }

            var creditPercent = 0
            val totalPayment = creditAmount.add(cashAmount)

            if (totalPayment > BigDecimal(0)) {
                creditPercent = calculatePercentage(creditAmount, totalPayment)
            }

            with(binding.paymentSummaryLayout) {
                animateProgressbar(progressbarPayment, creditPercent)
                progressbarPayment.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.orange))
                progressbarPayment.progressBackgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.red))
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

            with(binding.expensePatternLayout) {
                animateTextViewPercent(txtNormalPercent, ANIMATION_DURATION, 0, normalPercent)
                animateTextViewPercent(txtWastePercent, ANIMATION_DURATION, 0, wastePercent)
                animateTextViewPercent(txtInsertPercent, ANIMATION_DURATION, 0, investPercent)
            }
        }
        else if (totalExpense == BigDecimal(0)) {
            with(binding.expensePatternLayout) {
                animateTextViewPercent(txtNormalPercent, ANIMATION_DURATION, 0, 0)
                animateTextViewPercent(txtWastePercent, ANIMATION_DURATION, 0, 0)
                animateTextViewPercent(txtInsertPercent, ANIMATION_DURATION, 0, 0)
            }
        }

        with(binding.expensePatternLayout) {
            animateProgressbar(progressbarNormal, normalPercent)
            animateProgressbar(progressbarWaste, wastePercent)
            animateProgressbar(progressbarInvest, investPercent)
        }

    }

    private fun setCurrentDate(date: Date) {
        binding.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(date)
        binding.compactcalendarView.setCurrentDate(date)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, getString(R.string.toast_exit),
                Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(TIME_EXIT_DELAY) {
                doubleBackToExit = false
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

    companion object {
        const val DRAWER_CLOSE_DELAY = 300L
        const val TIME_EXIT_DELAY = 1500L

        const val ANIMATION_DURATION = 1000L
        const val START_VALUE = 0

        fun start(context: Context) {
            Intent(context, DashboardActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}
