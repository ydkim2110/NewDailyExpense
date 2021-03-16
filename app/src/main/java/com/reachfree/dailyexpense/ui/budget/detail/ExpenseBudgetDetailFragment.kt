package com.reachfree.dailyexpense.ui.budget.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.CategoryExpenseByDate
import com.reachfree.dailyexpense.databinding.ExpenseBudgetDetailFragmentBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.CurrencyUtils.changeAmountByCurrency
import com.reachfree.dailyexpense.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ExpenseBudgetDetailFragment : BaseDialogFragment<ExpenseBudgetDetailFragmentBinding>() {

    private val viewModel: ExpenseBudgetDetailViewModel by viewModels()

    private val expenseBudgetDetailAdapter: ExpenseBudgetDetailAdapter by lazy {
        ExpenseBudgetDetailAdapter()
    }

    private lateinit var category: Category
    private lateinit var currentDate: Date

    private var startOfBeforeFiveMonth = 0L
    private var startOfMonth = 0L
    private var endOfMonth = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        category = AppUtils.getExpenseCategory(requireArguments().getString(CATEGORY_ID, Constants.FOOD_DRINKS))
        currentDate = Date(requireArguments().getLong(DATE, Date().time))

        val dateTypeYearMonth = AppUtils.convertDateToYearMonth(currentDate)
        startOfBeforeFiveMonth = AppUtils.startOfBeforeFiveMonth(dateTypeYearMonth)
        startOfMonth = AppUtils.startOfMonth(dateTypeYearMonth)
        endOfMonth = AppUtils.endOfMonth(dateTypeYearMonth)

        viewModel.getExpenseBudgetLiveData(startOfMonth, endOfMonth, category.id)
        viewModel.getAllTransactionByCategoryLiveData(startOfMonth, endOfMonth, category.id)
        viewModel.getAllCategoryTransactionLiveData(startOfBeforeFiveMonth, endOfMonth, category.id)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ExpenseBudgetDetailFragmentBinding {
        return ExpenseBudgetDetailFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = resources.getString(category.visibleNameResId)
        binding.appBar.btnAction.visibility = View.VISIBLE
        binding.appBar.btnAction.load(R.drawable.ic_delete)
    }

    private fun setupView() {
        binding.imgCategoryIcon.load(category.iconResId)
        binding.imgCategoryIcon.changeBackgroundTintColor(category.backgroundColor)
    }

    private fun setupRecyclerView() {
        binding.recyclerTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewHandler() {
        binding.appBar.btnBack.setOnSingleClickListener {
            dismiss()
        }

        binding.appBar.btnAction.setOnSingleClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun subscribeToObserver() {
        expenseBudgetDetailAdapter.setOnItemClickListener { transaction ->
            when (transaction.type) {
                EXPENSE.ordinal -> {
                    val addExpenseFragment = AddExpenseFragment.newInstance(transaction)
                    addExpenseFragment.show(childFragmentManager, null)
                }
                INCOME.ordinal -> {
                    val addIncomeFragment = AddIncomeFragment.newInstance(transaction)
                    addIncomeFragment.show(childFragmentManager, null)
                }
            }
        }

        viewModel.expenseByCategoryList.observe(this) { result ->
            binding.recyclerTransaction.adapter = expenseBudgetDetailAdapter
            expenseBudgetDetailAdapter.submitList(result)
        }

        viewModel.expenseBudgetByCategory.observe(this) { result ->
            result?.let {
                val category = AppUtils.getExpenseCategory(result.categoryId)

                val budgetedAmount = it.budgetAmount!!
                val spentAmount = it.sumByCategory!!
                val leftAmount = budgetedAmount.minus(spentAmount)
                var leftBudgetPercent = 0

                if (budgetedAmount > BigDecimal(0)) {
                    leftBudgetPercent = if (spentAmount >= BigDecimal(0)) {
                        AppUtils.calculatePercentage(spentAmount, budgetedAmount)
                    } else {
                        100
                    }
                }

                binding.progressbarBudget.changeTintColor(category.backgroundColor)

                binding.txtBudgetedAmount.text = changeAmountByCurrency(budgetedAmount)
                binding.txtLeftToSpendAmount.text = changeAmountByCurrency(leftAmount)

                binding.progressbarBudget.animateProgressbar(leftBudgetPercent)

                if (spentAmount > budgetedAmount) {
                    binding.txtBudgetComment.text = requireContext().resources.getString(R.string.text_budget_you_spent_this_month_exceed, changeAmountByCurrency(spentAmount))
                } else {
                    binding.txtBudgetComment.text = requireContext().resources.getString(R.string.text_budget_you_spent_this_month, changeAmountByCurrency(spentAmount))
                }
            }
        }

        viewModel.expenseBudgetByCategoryList.observe(this) { result ->
            setupBarChart(result)
        }

    }

    private fun setupBarChart(result: List<CategoryExpenseByDate>) {
        val barEntries = ArrayList<BarEntry>()
        val barColors = ArrayList<Int>()

        val labels = AppUtils.convertDateToYearMonth(currentDate)

        val xAxisLabel = arrayListOf(
            labels.minusMonths(4).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(3).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(2).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM")),
            labels.format(DateTimeFormatter.ofPattern("MMM"))
        )

        var index = 0f
        for (i in 4 downTo 0) {
            var isExist = false
            for (j in result.indices) {
                if (labels.minusMonths(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM")) == result[j].date) {
                    barEntries.add(BarEntry(index, result[j].sumByCategory!!.toFloat()))
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

        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.md_amber_500))

        binding.barChartExpenseBudget.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            labelCount = barEntries.size
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    axis?.textSize = 14f
                    axis?.yOffset = 10f
                    return xAxisLabel[value.toInt()]
                }
            }
            setDrawLabels(true)
            axisLineColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            textColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            setDrawGridLines(false)
        }
        binding.barChartExpenseBudget.axisLeft.apply {
            isEnabled = false
        }
        binding.barChartExpenseBudget.axisRight.apply {
            isEnabled = false
        }

        binding.barChartExpenseBudget.axisLeft.axisMinimum = 0f

        binding.barChartExpenseBudget.apply {
            description.text = "Expense Budget"
            description.isEnabled = false
            legend.isEnabled = false

            extraBottomOffset = 10f

            setScaleEnabled(false)
        }

        val barDataSet = BarDataSet(barEntries, "Budget").apply {
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            valueTextSize = 14f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) {
                        ""
                    } else {
                        changeAmountByCurrency(BigDecimal(value.toString()))
                    }
                }
            }
            colors = barColors
        }

        binding.barChartExpenseBudget.data = BarData(barDataSet).apply {
            barWidth = 0.4f
        }

        binding.barChartExpenseBudget.animateY(ANIMATION_DURATION, Easing.EaseInOutQuad)

        binding.barChartExpenseBudget.invalidate()
    }

    private fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)

        builder.setTitle(getString(R.string.dialog_delete_title))
        builder.setMessage(getString(R.string.dialog_delete_description_number))

        builder.setPositiveButton(getString(R.string.dialog_delete_positive)) { dialog, _ ->
            viewModel.deleteExpenseBudgetByCategoryId(category.id)
            dialog.dismiss()
            dismiss()
        }
        builder.setNegativeButton(getString(R.string.dialog_delete_negative)) { dialog, _ -> dialog.dismiss() }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setBackgroundResource(android.R.color.transparent)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setBackgroundResource(android.R.color.transparent)
        }

        alertDialog.show()
    }

    companion object {
        private const val ANIMATION_DURATION = 1000
        private const val DATE = "date"
        private const val CATEGORY_ID = "categoryId"
        const val TAG = "ExpenseBudgetDetailFragment"
        fun newInstance(categoryId: String, date: Long) = ExpenseBudgetDetailFragment().apply {
            arguments = bundleOf(CATEGORY_ID to categoryId, DATE to date)
        }
    }
}