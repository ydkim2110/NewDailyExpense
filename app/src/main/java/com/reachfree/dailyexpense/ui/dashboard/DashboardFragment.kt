package com.reachfree.dailyexpense.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.reachfree.dailyexpense.MainActivity
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.DashboardFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import timber.log.Timber
import java.util.*

class DashboardFragment : BaseFragment<DashboardFragmentBinding>() {

    private var isExpanded = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DashboardFragmentBinding {
        return DashboardFragmentBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentDate(Date())
//        binding.datePickerButton.setOnClickListener {
//            val rotation = if (isExpanded) 0f else 180f
//            ViewCompat.animate(binding.datePickerArrow).rotation(rotation).start()
//            isExpanded = !isExpanded
//            binding.appBar.setExpanded(isExpanded, true)
//        }

//        setupToolbar()
//        setupCalendarView()
        setupProgressbar()
        setupPiechart()
    }
//
//    private fun setupToolbar() {
//        (activity as MainActivity).setSupportActionBar(binding.toolbar)
//        (activity as MainActivity).supportActionBar?.setTitle(R.string.app_name)
//        binding.title.text = resources.getString(R.string.app_name)
//    }
//
//    private fun setupCalendarView() {
//        binding.compactcalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault())
//        binding.compactcalendarView.setShouldDrawDaysHeader(true)
//        binding.compactcalendarView.setListener(object :
//            CompactCalendarView.CompactCalendarViewListener {
//            override fun onDayClick(dateClicked: Date?) {
//                Timber.d("$dateClicked")
//                setSubTitle(AppUtils.yearMonthDateFormat.format(dateClicked))
//            }
//
//            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
//                setSubTitle(AppUtils.yearMonthDateFormat.format(firstDayOfNewMonth))
//            }
//
//        })
//    }

    private fun setupProgressbar() {
        AppUtils.animateProgressbar(binding.expensePatternLayout.progressbarNormal, 70)
        AppUtils.animateProgressbar(binding.expensePatternLayout.progressbarWaste, 90)
        AppUtils.animateProgressbar(binding.expensePatternLayout.progressbarInvest, 40)
    }

    private fun setupPiechart() {
//        val pieDataSet = PieDataSet(listOf(PieEntry(1f, getString(R.string.chart_default))),
//            getString(R.string.chart_record))
//        val colors = arrayListOf(ContextCompat.getColor(requireContext(), R.color.colorGray))
//
//        pieDataSet.colors = colors
//
//        val pieData = PieData(pieDataSet)
//        pieData.setDrawValues(false)
//        binding.chartLayout.pieChart.run {
//            if (data == pieData) return
//            data = pieData
//            legend.isEnabled = false
//            description = null
//            holeRadius = 75f
//            setHoleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
//            setDrawEntryLabels(false)
//            animateY(Constants.CHART_ANIMATION_SPEED, Easing.EaseInQuart)
//            invalidate()
//        }
//
//        setupAmount()
    }

    private fun setCurrentDate(date: Date) {
        setSubTitle(AppUtils.yearMonthDateFormat.format(date))
//        binding.compactcalendarView.setCurrentDate(date)
    }

    private fun setSubTitle(subtitle: String) {
//        binding.datePickerTextView.text = subtitle
    }

    private fun setupAmount() {
//        with(binding) {
//            chartLayout.txtNormalExpense.text = "2,300,122원"
//            chartLayout.txtWasteExpense.text = "40,000원"
//            chartLayout.txtInvestExpense.text = "300,000원"
//        }
    }

}