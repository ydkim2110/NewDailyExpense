package com.reachfree.dailyexpense.ui.dashboard.payment

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.PaymentFragmentBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PAYMENT.CASH
import com.reachfree.dailyexpense.util.Constants.PAYMENT.CREDIT
import com.reachfree.dailyexpense.util.Constants.SortType
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class PaymentFragment : BaseDialogFragment<PaymentFragmentBinding>() {

    private val viewModel: PaymentViewModel by viewModels()
    private val paymentAdapter: PaymentAdapter by lazy {
        PaymentAdapter()
    }

    private lateinit var currentDate: Date

    private var startOfBeforeFiveMonth = 0L
    private var startOfMonth = 0L
    private var endOfMonth = 0L

    private var isOptionsExpanded = false
    private lateinit var filterSortArray: Array<String>
    private lateinit var filterTransactionArray: Array<String>
    private var selectedSortIndex = 0
    private var selectedTransactionIndex = 0
    private var beforeSelectedTransactionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        currentDate = Date(requireArguments().getLong(DATE, Date().time))

        filterSortArray = resources.getStringArray(R.array.filter_sort_options)
        filterTransactionArray = resources.getStringArray(R.array.filter_payment_options)

        startOfBeforeFiveMonth = AppUtils.startOfBeforeFiveMonth(AppUtils.convertDateToYearMonth(currentDate))
        startOfMonth = AppUtils.startOfMonth(AppUtils.convertDateToYearMonth(currentDate))
        endOfMonth = AppUtils.endOfMonth(AppUtils.convertDateToYearMonth(currentDate))

        viewModel.getAllTransactionByPaymentLiveData(startOfBeforeFiveMonth, endOfMonth)
        viewModel.getTransactionSortedBy(
            SortType.AMOUNT,
            startOfMonth,
            endOfMonth,
            intArrayOf(EXPENSE.ordinal),
            intArrayOf(CREDIT.ordinal, CASH.ordinal)
        )
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PaymentFragmentBinding {
        return PaymentFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupOptions()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = getString(R.string.toolbar_title_payment)
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        binding.recyclerTransaction.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
        }

        paymentAdapter.setOnItemClickListener { transaction ->
            when (transaction.type) {
                EXPENSE.ordinal -> {
                    val addExpenseFragment = AddExpenseFragment.newInstance(transaction)
                    addExpenseFragment.show(childFragmentManager, null)
                }
            }
        }
    }

    private fun setupOptions() {
        binding.relativeLayoutOptionsFragment.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.txtSwitcherSortBy.setFactory {
            val textView = TextView(requireContext())

            textView.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
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
            val textView = TextView(requireContext())

            textView.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
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
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CREDIT.ordinal)
                )
            }
            1 -> {
                viewModel.getTransactionSortedBy(
                    sortType,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CASH.ordinal)
                )
            }
            else -> {
                viewModel.getTransactionSortedBy(
                    sortType,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CREDIT.ordinal, CASH.ordinal)
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
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CREDIT.ordinal, CASH.ordinal)
                )
            }
            selectedTransactionIndex > 0 -> {
                beforeSelectedTransactionIndex = selectedTransactionIndex
                selectedTransactionIndex++
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CASH.ordinal)
                )
            }
            else -> {
                beforeSelectedTransactionIndex = selectedTransactionIndex
                selectedTransactionIndex++
                viewModel.getTransactionSortedBy(
                    SortType.AMOUNT,
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(EXPENSE.ordinal),
                    intArrayOf(CREDIT.ordinal)
                )
            }
        }
    }

    private fun subscribeToObserver() {
        viewModel.allTransactionByPayment.observe(this) { result ->
            setupBarChart(result)
        }
        viewModel.transactionListByPayment.observe(this) { result ->
            binding.recyclerTransaction.adapter = paymentAdapter
            paymentAdapter.submitList(result)
        }
    }

    private fun setupBarChart(result: List<PaymentChartModel>) {
        val creditBarColors = ArrayList<Int>()
        val cashBarColors = ArrayList<Int>()

        val labels = AppUtils.convertDateToYearMonth(currentDate)

        val xAxisLabel = arrayListOf(
            labels.minusMonths(4).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(3).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(2).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM")),
            labels.format(DateTimeFormatter.ofPattern("MMM"))
        )

        //TODO: 백만원 단위로 변경
        val count = result.count { it.amount!! >= BigDecimal(1000) }
        val newList = result
            .map { AppUtils.divideBigDecimal(it.amount!!, BigDecimal(1000)) }

        Timber.d("list $newList")

        val creditResult = result.filter { it.payment == CREDIT.ordinal }
        val cashResult = result.filter { it.payment == CASH.ordinal }

        val creditBarEntries = setDataToCalendar(labels, creditResult)
        val cashBarEntries = setDataToCalendar(labels, cashResult)

        creditBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_orange_300))
        creditBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_orange_300))
        creditBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_orange_300))
        creditBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_orange_300))
        creditBarColors.add(ContextCompat.getColor(requireContext(), R.color.orange))

        cashBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_red_500))
        cashBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_red_500))
        cashBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_red_500))
        cashBarColors.add(ContextCompat.getColor(requireContext(), R.color.md_red_500))
        cashBarColors.add(ContextCompat.getColor(requireContext(), R.color.dark_red))

        binding.barChartPaymentAmount.axisLeft.apply { isEnabled = false }
        binding.barChartPaymentAmount.axisRight.apply { isEnabled = false }

        binding.barChartPaymentAmount.xAxis.apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            textSize = 14f

            yOffset = 10f
            axisLineColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)

            valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            setCenterAxisLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            isGranularityEnabled = true
            setDrawGridLines(false)
        }

        binding.barChartPaymentAmount.axisLeft.axisMinimum = 0f

        val expenseBarDataSet = BarDataSet(cashBarEntries, "Expense").apply {
            colors = cashBarColors

            valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) {
                        ""
                    } else {
                        CurrencyUtils.changeAmountByCurrency(BigDecimal(value.toString()))
                    }
                }
            }
        }
        val incomeBarDataSet = BarDataSet(creditBarEntries, "Income").apply {
            colors = creditBarColors

            valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) {
                        ""
                    } else {
                        CurrencyUtils.changeAmountByCurrency(BigDecimal(value.toString()))
                    }
                }
            }
        }

        binding.barChartPaymentAmount.data = BarData(expenseBarDataSet, incomeBarDataSet).apply {
            barWidth = 0.3f
        }

        binding.barChartPaymentAmount.apply {
            description.isEnabled = false
            legend.isEnabled = false

            extraBottomOffset = 10f

            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = 5f

            setScaleEnabled(false)
        }
        // (0.3 + 0.05) * 2 + 0.3 = 1.00
        binding.barChartPaymentAmount.groupBars(0f, 0.3f, 0.05f)
        binding.barChartPaymentAmount.animateY(ANIMATION_DURATION, Easing.EaseInOutQuad)
        binding.barChartPaymentAmount.invalidate()
    }

    private fun setDataToCalendar(labels: YearMonth, list: List<PaymentChartModel>): List<BarEntry> {
        val barEntries = ArrayList<BarEntry>()
        var index = 0f

        for (i in 4 downTo 0) {
            var isExist = false
            for (j in list.indices) {
                if (labels.minusMonths(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM")) == list[j].date) {
                    barEntries.add(BarEntry(index, list[j].amount!!.toFloat()))
                    index++
                    isExist = true
                    continue
                }
            }
            if (!isExist) {
                barEntries.add(BarEntry(index, 0f))
                index++
            }
        }

        return barEntries
    }
 
    companion object {
        const val TAG = "PaymentFragment"

        private const val ANIMATION_DURATION = 1000
        private const val ROTATION = "rotation"
        private const val ROTATION_ANIM_DURATION = 250L
        private const val DATE = "date"

        fun newInstance(date: Long) = PaymentFragment().apply {
            arguments = bundleOf(DATE to date)
        }
    }
}