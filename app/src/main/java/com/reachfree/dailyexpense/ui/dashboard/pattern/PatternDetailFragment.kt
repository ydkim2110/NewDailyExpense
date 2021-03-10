package com.reachfree.dailyexpense.ui.dashboard.pattern

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.databinding.PatternDetailFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.category.PatternCategoryFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.Status.*
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class PatternDetailFragment : BaseDialogFragment<PatternDetailFragmentBinding>() {

    private val viewModel: PatternDetailViewModel by viewModels()

    private val patternDetailAdapter: PatternDetailAdapter by lazy {
        PatternDetailAdapter()
    }

    private var startOfMonth = 0L
    private var endOfMonth = 0L

    private var expensePatternType = Constants.EXPENSE_PATTERN_TOTAL
    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        expensePatternType = requireArguments().getInt(PATTERN, Constants.EXPENSE_PATTERN_TOTAL)
        currentDate = Date(requireArguments().getLong(DATE, Date().time))

        startOfMonth = AppUtils.startOfMonth(AppUtils.convertDateToYearMonth(currentDate))
        endOfMonth = AppUtils.endOfMonth(AppUtils.convertDateToYearMonth(currentDate))
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PatternDetailFragmentBinding {
        return PatternDetailFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupView() {
        binding.txtViewNoItem.text = requireContext().resources.getString(R.string.text_no_transaction)
        binding.imgNoItem.load(R.drawable.logo)

        binding.recyclerCategory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        when (expensePatternType) {
            Constants.EXPENSE_PATTERN_TOTAL -> {
                binding.appBar.txtToolbarTitle.text = resources.getString(R.string.text_total_expense)
                hideContentLayout()
                viewModel.getTransactionByPatternToCategory(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal, WASTE.ordinal, INVEST.ordinal)
                )
            }
            Constants.EXPENSE_PATTERN_NORMAL -> {
                binding.appBar.txtToolbarTitle.text = resources.getString(R.string.text_normal_expense)
                hideContentLayout()
                viewModel.getTransactionByPatternToCategory(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal)
                )
            }
            Constants.EXPENSE_PATTERN_WASTE -> {
                binding.appBar.txtToolbarTitle.text = resources.getString(R.string.text_waste_expense)
                hideContentLayout()
                viewModel.getTransactionByPatternToCategory(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(WASTE.ordinal)
                )
            }
            Constants.EXPENSE_PATTERN_INVEST -> {
                binding.appBar.txtToolbarTitle.text = resources.getString(R.string.text_invest_expense)
                hideContentLayout()
                viewModel.getTransactionByPatternToCategory(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INVEST.ordinal)
                )
            }

        }
    }

    private fun setupViewHandler() {
        binding.appBar.btnBack.setOnSingleClickListener {
            dismiss()
        }

        patternDetailAdapter.setOnItemClickListener { categoryId ->
            val patternCategoryFragment = PatternCategoryFragment.newInstance(expensePatternType, categoryId, currentDate.time)
            patternCategoryFragment.show(childFragmentManager, PatternCategoryFragment.TAG)
        }
    }

    private fun subscribeToObserver() {
        viewModel.transactionByPatternToCategory.observe(this) { result ->
            val sorted = result?.sortedByDescending { it.sumByCategory }!!
            binding.recyclerCategory.adapter = patternDetailAdapter
            patternDetailAdapter.submitList(sorted)

            if (sorted.isEmpty()) {
                if (binding.linearLayoutNoItem.isGone) {
                    binding.linearLayoutNoItem.isVisible = true
                }
            } else {
                if (binding.linearLayoutNoItem.isVisible) {
                    binding.linearLayoutNoItem.isVisible = false
                }
                showContentLayout()
            }

            setupPieChart(sorted)
        }

    }

    private fun hideContentLayout() {
        binding.scrollViewContent.visibility = View.GONE
    }

    private fun showContentLayout() {
        binding.scrollViewContent.visibility = View.VISIBLE
    }

    private fun setupPieChart(data: List<ExpenseByCategory>?) {
        val pieEntries = ArrayList<PieEntry>()
        val pieColors = ArrayList<Int>()

        var drawable: Drawable
        data?.let { it ->
            it.forEach { expenseByCategory ->
                val category = AppUtils.getExpenseCategory(expenseByCategory.categoryId)
                drawable = ResourcesCompat.getDrawable(
                    requireContext().resources,
                    category.iconResId,
                    null
                )!!

                val newDrawable = AppUtils.scaleIcon(requireContext(), drawable, 0.7f)

                newDrawable.setTint(Color.parseColor("#FFFFFF"))
                pieColors.add(ContextCompat.getColor(requireContext(), category.backgroundColor))
                pieEntries.add(PieEntry(expenseByCategory.sumByCategory!!.toFloat(), newDrawable))
                if (pieEntries.size > 4) {
                    return@let
                }
            }
        }

        val dataSet = PieDataSet(pieEntries, "").apply {
            setDrawValues(false)
            sliceSpace = 3f
            selectionShift = 5f
            valueTextSize = 12f
            colors = pieColors
        }

        val pieData = PieData(dataSet)

        val dateString = AppUtils.yearDashMonthDateFormat.format(currentDate)

        val ssb = SpannableStringBuilder(dateString)
            .append("\n")
            .append(CurrencyUtils.changeAmountByCurrency(data!!.sumOf { it.sumByCategory!! }))

        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)),
            0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(AbsoluteSizeSpan(50), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ssb.setSpan(StyleSpan(Typeface.BOLD), 7, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorExpense)),
            7, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(AbsoluteSizeSpan(60), 7, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.patternChartLayout.patternPieChart.apply {
            setData(pieData)
            legend.isEnabled = false
            description.isEnabled = false

            setUsePercentValues(true)
            isDrawHoleEnabled = true

            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(55)

            setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorPieChartHoleBackground))

            holeRadius = 70f
            transparentCircleRadius = 55f

            setDrawCenterText(true)

            rotationAngle = 0f
            isRotationEnabled = true

            centerText = ssb
            animateY(ANIMATION_DURATION, Easing.EaseInOutQuad)

            invalidate()
        }

    }

    companion object {
        private const val ANIMATION_DURATION = 1000
        private const val PATTERN = "pattern"
        private const val DATE = "date"

        fun newInstance(pattern: Int, date: Long) = PatternDetailFragment().apply {
            arguments = bundleOf(PATTERN to pattern, DATE to date)
        }
    }

}