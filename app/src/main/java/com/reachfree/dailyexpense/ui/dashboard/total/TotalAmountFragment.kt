package com.reachfree.dailyexpense.ui.dashboard.total

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.CategoryExpenseByDate
import com.reachfree.dailyexpense.databinding.TotalAmountFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.budget.detail.ExpenseBudgetDetailFragment
import com.reachfree.dailyexpense.util.AppUtils
import timber.log.Timber
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TotalAmountFragment : BaseDialogFragment<TotalAmountFragmentBinding>() {

    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        currentDate = Date(requireArguments().getLong(DATE, Date().time))
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TotalAmountFragmentBinding {
        return TotalAmountFragmentBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBarChart()
    }

    private fun setupBarChart() {
//        val barEntries = ArrayList<BarEntry>()
        val barColors = ArrayList<Int>()

        val labels = AppUtils.convertDateToYearMonth(currentDate)

        val xAxisLabel = arrayListOf(
            labels.minusMonths(4).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(3).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(2).format(DateTimeFormatter.ofPattern("MMM")),
            labels.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM")),
            labels.format(DateTimeFormatter.ofPattern("MMM"))
        )

//        var index = 0f
//        for (i in 4 downTo 0) {
//            var isExist = false
//            for (j in result.indices) {
//                if (labels.minusMonths(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM")) == result[j].date) {
//                    barEntries.add(BarEntry(index, result[j].sumByCategory!!.toFloat()))
//                    index++
//                    isExist = true
//                    continue
//                }
//            }
//            if (!isExist) {
//                barEntries.add(BarEntry(index, 0f))
//                index++
//            }
//        }

        val group1 = ArrayList<BarEntry>()
        group1.add(BarEntry(0f, 230000f))
        group1.add(BarEntry(1f, 330000f))
        group1.add(BarEntry(2f, 430000f))
        group1.add(BarEntry(3f, 530000f))
        group1.add(BarEntry(4f, 630000f))

        val group2 = ArrayList<BarEntry>()
        group2.add(BarEntry(0f, 130000f))
        group2.add(BarEntry(1f, 430000f))
        group2.add(BarEntry(2f, 330000f))
        group2.add(BarEntry(3f, 430000f))
        group2.add(BarEntry(4f, 530000f))

        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.purple_200))
        barColors.add(ContextCompat.getColor(requireContext(), R.color.md_amber_500))

        binding.barChartTotalAmount.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            labelCount = group1.size
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    axis?.textSize = 14f
                    axis?.yOffset = 10f
                    Timber.d("value: $value")
                    return xAxisLabel[value.toInt()]
                }
            }
            setDrawLabels(true)
            axisLineColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            textColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            setDrawGridLines(false)
        }
        binding.barChartTotalAmount.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            setDrawGridLines(false)
            isEnabled = false
        }
        binding.barChartTotalAmount.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            setDrawGridLines(false)
            isEnabled = false
        }
        binding.barChartTotalAmount.apply {
            description.text = "Expense Budget"
            description.isEnabled = false
            legend.isEnabled = false

            extraBottomOffset = 10f

            setScaleEnabled(false)
            setFitBars(true)
        }


        val bardataset1 = BarDataSet(group1, "Brand 1").apply {
            color = Color.RED

            valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return AppUtils.changeAmountByCurrency(BigDecimal(value.toString()))
                }
            }
        }
        val bardataset2 = BarDataSet(group2, "Brand 2").apply {
            color = ContextCompat.getColor(requireContext(), R.color.orange)

            valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorTextPrimary)
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return AppUtils.changeAmountByCurrency(BigDecimal(value.toString()))
                }
            }
        }

        binding.barChartTotalAmount.data = BarData(bardataset1, bardataset2).apply {
            barWidth = 0.3f
        }

        binding.barChartTotalAmount.groupBars(0f, 0.25f, 0.05f)
        binding.barChartTotalAmount.invalidate()
    }

    companion object {
        const val TAG = "TotalAmountFragment"
        private const val DATE = "date"

        fun newInstance(date: Long) = TotalAmountFragment().apply {
            arguments = bundleOf(DATE to date)
        }
    }
}