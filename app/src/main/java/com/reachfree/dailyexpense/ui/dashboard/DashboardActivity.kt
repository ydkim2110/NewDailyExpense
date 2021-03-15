package com.reachfree.dailyexpense.ui.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.DashboardActivityBinding
import com.reachfree.dailyexpense.manager.NotificationServiceManager
import com.reachfree.dailyexpense.manager.SessionManager
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.bottomsheet.SelectTypeBottomSheet
import com.reachfree.dailyexpense.ui.budget.ExpenseBudgetActivity
import com.reachfree.dailyexpense.ui.calendar.CalendarActivity
import com.reachfree.dailyexpense.ui.dashboard.pattern.PatternDetailFragment
import com.reachfree.dailyexpense.ui.dashboard.payment.PaymentFragment
import com.reachfree.dailyexpense.ui.dashboard.total.TotalAmountFragment
import com.reachfree.dailyexpense.ui.settings.PREF_CURRENCY
import com.reachfree.dailyexpense.ui.settings.SettingsActivity
import com.reachfree.dailyexpense.ui.transaction.TransactionActivity
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.AppUtils.calculatePercentage
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.ACTION_SHOW_ADD_EXPENSE
import com.reachfree.dailyexpense.util.Constants.FIRST_DAY_OF_WEEK
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.PAYMENT
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_EVERY_DAY_NOTIFICATION
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_FULLNAME
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_NICKNAME
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.extension.*
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.time.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DashboardActivity :
    BaseActivity<DashboardActivityBinding>({ DashboardActivityBinding.inflate(it) }),
    RecentTGListHeaderAdapter.OnItemClickListener {

    @Inject lateinit var sessionManager: SessionManager

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentTGListHeaderAdapter: RecentTGListHeaderAdapter

    private val selectTypeBottomSheet by lazy {
        SelectTypeBottomSheet()
    }

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private lateinit var updateListener: InstallStateUpdatedListener

    private var doubleBackToExit = false
    private var isExpanded = false
    private var currentDate = Date()

    private var currentStartDate: Long = 0L
    private var currentEndDate: Long = 0L
    private var currentRecentStartDate: Long = 0L
    private var currentRecentEndDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForUpdates()

        Constants.currencySymbol = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol

        Currency.fromCode(sessionManager.getCurrencyCode())?.let {
            Constants.currentCurrency = it
        }

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

        binding.recentTransactionsLayout.noItemLayout.txtNoItem.text = getString(R.string.text_no_transaction)
        binding.recentTransactionsLayout.noItemLayout.imgNoItem.load(R.drawable.logo)

        setupToolbar()
        setupNavigation()
        setupCalendarView()
        setupRecyclerView()
        setupViewHandler()
        setupSelectTypeListener()
        subscribeToObserver()
        setupEverydayNotification()

        sessionManager.getPrefs().registerOnSharedPreferenceChangeListener(sharedPrefListener)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("onNewIntent")

        if (intent?.action == ACTION_SHOW_ADD_EXPENSE) {
            AddExpenseFragment.newInstance().apply { show(supportFragmentManager, null) }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.IMMEDIATE,
                    this,
                    REQUEST_CODE_UPDATE
                )
            }
        }
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { handleUpdate(appUpdateManager, appUpdateInfoTask) }
    }

    private fun handleUpdate(
        manager: AppUpdateManager,
        info: Task<AppUpdateInfo>
    ) {
        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
            handleImmediateUpdate(info)
        } else if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
            handleFlexibleUpdate(info)
        }
    }

    private fun handleImmediateUpdate(info: Task<AppUpdateInfo>) {
        if(info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            appUpdateManager.startUpdateFlowForResult(info.result, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE)
        }
    }

    private fun handleFlexibleUpdate(info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            setUpdateAction(info)
        }
    }

    private fun setUpdateAction(info: Task<AppUpdateInfo>) {
        updateListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    launchRestartDialog(appUpdateManager)
                }
            }
        }
    }

    private fun launchRestartDialog(manager: AppUpdateManager) {
        val builder = AlertDialog.Builder(this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)

        builder.setTitle(getString(R.string.text_dialog_update_title))
        builder.setMessage(getString(R.string.text_dialog_update_message))

        builder.setPositiveButton(getString(R.string.text_dialog_update_restart)) { dialog, _ ->
            manager.completeUpdate()
            dialog.dismiss()
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setBackgroundResource(android.R.color.transparent)
        }

        alertDialog.show()
    }

    private val sharedPrefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
            Timber.d("key $key")
            if (PREF_CURRENCY == key) {
                Constants.currencySymbol = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol
                Currency.fromCode(sessionManager.getCurrencyCode())?.let {
                    Constants.currentCurrency = it
                }

                viewModel.dateForMonthly.value = listOf(currentStartDate, currentEndDate)
                viewModel.dateForRecent.value = listOf(currentRecentStartDate, currentRecentEndDate)
            } else if (PREF_KEY_NICKNAME == key || PREF_KEY_FULLNAME == key) {
                binding.includeDrawer.layoutHeader.txtNickname.text = sessionManager.getUser().nickname
            } else if (PREF_KEY_EVERY_DAY_NOTIFICATION == key) {
                setupEverydayNotification()
            }
    }

    private fun setupEverydayNotification() {
        if (sessionManager.getEverydayNotification()) {
            val alarmServiceManager = NotificationServiceManager(this)
            alarmServiceManager.setEverydayNotification()
        } else {
            val alarmServiceManager = NotificationServiceManager(this)
            alarmServiceManager.cancelEverydayNotification()
        }
    }

    private fun runAfterDrawerClose(action: () -> Unit) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        runDelayed(DRAWER_CLOSE_DELAY) { action() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar.toolbar)

        binding.appBar.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        with(binding.includeDrawer) {
            layoutTransaction.setOnSingleClickListener { runAfterDrawerClose { TransactionActivity.start(this@DashboardActivity) } }
            layoutExpenseBudget.setOnSingleClickListener { runAfterDrawerClose { ExpenseBudgetActivity.start(this@DashboardActivity) } }
            layoutCalendar.setOnSingleClickListener { runAfterDrawerClose { CalendarActivity.start(this@DashboardActivity) } }
            layoutRateApp.setOnSingleClickListener { runAfterDrawerClose { showRateApp() } }
            layoutSendFeedback.setOnSingleClickListener { runAfterDrawerClose { showSendFeedback() } }
            layoutSettings.setOnSingleClickListener { runAfterDrawerClose { SettingsActivity.start(this@DashboardActivity) } }
        }

        binding.includeDrawer.layoutHeader.txtNickname.text = sessionManager.getUser().nickname
    }

    private fun showRateApp() {
        val reviewManager = ReviewManagerFactory.create(this)
        val requestReviewFlow = reviewManager.requestReviewFlow()

        requestReviewFlow.addOnCompleteListener { req ->
            if (req.isSuccessful) {
                val reviewInfo = req.result
                val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {

                }
            } else {

            }
        }
    }

    private fun showSendFeedback() {
        var intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("ydkim2110@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_send_feedback_subject))
        }
        intent = Intent.createChooser(intent, getString(R.string.text_choose_application))
        startActivity(intent)
    }

    private fun setupNavigation() {
        val toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBar.toolbar,
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
        binding.appBar.compactcalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault())
        binding.appBar.compactcalendarView.setShouldDrawDaysHeader(true)
        binding.appBar.compactcalendarView.setDayColumnNames(resources.getStringArray(R.array.day_of_week))
        binding.appBar.compactcalendarView.setFirstDayOfWeek(FIRST_DAY_OF_WEEK)
        binding.appBar.compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                binding.appBar.toolbarTitle.text =
                    AppUtils.yearMonthDateFormat.format(dateClicked!!)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                binding.appBar.toolbarTitle.text =
                    AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth)
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
                binding.recentTransactionsLayout.noItemLayout.noItemLayout.visibility = View.VISIBLE
            } else {
                binding.recentTransactionsLayout.recyclerRecentTransaction.visibility = View.VISIBLE
                binding.recentTransactionsLayout.noItemLayout.noItemLayout.visibility = View.GONE
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
        binding.appBar.datePickerButton.setOnSingleClickListener {
            val rotation = if (isExpanded) 0f else 180f
            ViewCompat.animate(binding.appBar.datePickerArrow).rotation(rotation).start()
            isExpanded = !isExpanded
            binding.appBar.appBar.setExpanded(isExpanded, true)
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

        binding.paymentSummaryLayout.paymentSummaryLayout.setOnSingleClickListener {
            PaymentFragment.newInstance(currentDate.time).apply {
                show(supportFragmentManager, PaymentFragment.TAG)
            }
        }

        with(binding.expensePatternLayout) {
            btnTotalList.setOnSingleClickListener {
                showPatternDetailFragment(Constants.EXPENSE_PATTERN_TOTAL)
            }
            progressbarNormal.setOnSingleClickListener {
                showPatternDetailFragment(Constants.EXPENSE_PATTERN_NORMAL)
            }
            progressbarWaste.setOnSingleClickListener {
                showPatternDetailFragment(Constants.EXPENSE_PATTERN_WASTE)
            }
            progressbarInvest.setOnSingleClickListener {
                showPatternDetailFragment(Constants.EXPENSE_PATTERN_INVEST)
            }
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
                txtTotalAmount.animateAmount(incomes.minus(expenses))
                txtTotalIncome.animateAmount(incomes)
                txtTotalExpense.animateAmount(expenses)
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
                txtNormalSum.animateAmount(normalExpense)
                txtWasteSum.animateAmount(wasteExpense)
                txtInvestSum.animateAmount(investExpense)
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
                txtCreditAmount.animateAmount(creditAmount)
                txtCashAmount.animateAmount(cashAmount)
            }

            setupPaymentBarChart(creditAmount, cashAmount)
        }
    }

    private fun setupPaymentBarChart(creditAmount: BigDecimal, cashAmount: BigDecimal) {
        val barEntries = ArrayList<BarEntry>()
        val barColors = ArrayList<Int>()

        val creditPercent: Int
        var cashPercent = 0
        val totalPayment = creditAmount.add(cashAmount)

        if (totalPayment > BigDecimal(0)) {
            creditPercent = calculatePercentage(creditAmount, totalPayment)
            cashPercent = 100 - creditPercent

            barColors.clear()
            barColors.add(ContextCompat.getColor(this, R.color.orange))
            barColors.add(ContextCompat.getColor(this, R.color.dark_red))
        } else {
            creditPercent = 100
            barColors.clear()
            barColors.add(ContextCompat.getColor(this, R.color.gray))
            barColors.add(ContextCompat.getColor(this, R.color.gray))
        }

        barEntries.add(BarEntry(0f, floatArrayOf(creditPercent.toFloat(), cashPercent.toFloat())))



        val barDataSet = BarDataSet(barEntries, "Payment").apply {
            setDrawValues(false)
            colors = barColors
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 20f
        }

        with(binding.paymentSummaryLayout.barChartPaymentSummary) {
            xAxis.apply {
                isEnabled = false
                spaceMin = 0f
            }
            axisLeft.apply {
                setDrawGridLines(false)
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = 100f
            }
            axisRight.apply {
                setDrawGridLines(false)
                isEnabled = false
            }

            description.isEnabled = false
            legend.isEnabled = false
            setScaleEnabled(false)

            data = barData

            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
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
                txtNormalPercent.animatePercent(normalPercent)
                txtWastePercent.animatePercent(wastePercent)
                txtInsertPercent.animatePercent(investPercent)
            }
        }
        else if (totalExpense == BigDecimal(0)) {
            with(binding.expensePatternLayout) {
                txtNormalPercent.animatePercent(0)
                txtWastePercent.animatePercent(0)
                txtInsertPercent.animatePercent(0)
            }
        }

        with(binding.expensePatternLayout) {
            progressbarNormal.animateProgressbar(normalPercent)
            progressbarWaste.animateProgressbar(wastePercent)
            progressbarInvest.animateProgressbar(investPercent)
        }

    }

    private fun setCurrentDate(date: Date) {
        binding.appBar.toolbarTitle.text = AppUtils.yearMonthDateFormat.format(date)
        binding.appBar.compactcalendarView.setCurrentDate(date)
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

        private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
        private const val REQUEST_CODE_UPDATE = 1212

        fun start(context: Context) {
            Intent(context, DashboardActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}
