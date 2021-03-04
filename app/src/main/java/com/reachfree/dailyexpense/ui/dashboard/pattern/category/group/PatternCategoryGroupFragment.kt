package com.reachfree.dailyexpense.ui.dashboard.pattern.category.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.dailyexpense.databinding.PatternCategoryGroupFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.category.PatternCategoryViewModel
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.FOOD_DRINKS
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PatternCategoryGroupFragment : BaseFragment<PatternCategoryGroupFragmentBinding>() {

    private val viewModel: PatternCategoryViewModel by viewModels()

    private val patternCategoryGroupAdapter: PatternCategoryGroupAdapter by lazy {
        PatternCategoryGroupAdapter()
    }

    private var startOfMonth = 0L
    private var endOfMonth = 0L

    private var expensePatternType = Constants.EXPENSE_PATTERN_TOTAL
    private var categoryId = FOOD_DRINKS

    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getInt(PATTERN, NORMAL.ordinal).also { expensePatternType = it }
        requireArguments().getString(CATEGORY_ID, FOOD_DRINKS).also { categoryId = it }
        requireArguments().getLong(DATE, Date().time).also { currentDate = Date(it) }

        startOfMonth = AppUtils.startOfMonth(AppUtils.convertDateToYearMonth(currentDate))
        endOfMonth = AppUtils.endOfMonth(AppUtils.convertDateToYearMonth(currentDate))
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PatternCategoryGroupFragmentBinding {
        return PatternCategoryGroupFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupView() {
        when (expensePatternType) {
            Constants.EXPENSE_PATTERN_TOTAL -> {
                viewModel.getSubCategoryGroupLiveData(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal, WASTE.ordinal, INVEST.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_NORMAL -> {
                viewModel.getSubCategoryGroupLiveData(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_WASTE -> {
                viewModel.getSubCategoryGroupLiveData(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(WASTE.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_INVEST -> {
                viewModel.getSubCategoryGroupLiveData(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INVEST.ordinal),
                    categoryId
                )
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerCategoryDetail.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObserver() {
        viewModel.subCategoryListLiveData.observe(viewLifecycleOwner) {
            binding.recyclerCategoryDetail.adapter = patternCategoryGroupAdapter
            patternCategoryGroupAdapter.submitList(it)
        }
    }
    
    companion object {
        private const val PATTERN = "pattern"
        private const val CATEGORY_ID = "categoryId"
        private const val DATE = "date"

        fun newInstance(
            pattern: Int,
            categoryId: String,
            date: Long
        ) = PatternCategoryGroupFragment().apply {
            arguments = bundleOf(PATTERN to pattern, CATEGORY_ID to categoryId, DATE to date)
        }
    }

}