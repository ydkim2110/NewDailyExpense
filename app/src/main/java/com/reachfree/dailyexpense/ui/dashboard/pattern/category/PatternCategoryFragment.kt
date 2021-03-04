package com.reachfree.dailyexpense.ui.dashboard.pattern.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.PatternCategoryFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.category.group.PatternCategoryGroupFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.category.list.PatternCategoryListFragment
import com.reachfree.dailyexpense.ui.transaction.TabAdapter
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.FOOD_DRINKS
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PatternCategoryFragment : BaseDialogFragment<PatternCategoryFragmentBinding>() {

    private var expensePatternType = Constants.EXPENSE_PATTERN_TOTAL
    private var categoryId = FOOD_DRINKS
    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        expensePatternType = requireArguments().getInt(PATTERN, Constants.EXPENSE_PATTERN_TOTAL)
        categoryId = requireArguments().getString(CATEGORY_ID, FOOD_DRINKS)
        currentDate = Date(requireArguments().getLong(DATE, Date().time))
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PatternCategoryFragmentBinding {
        return PatternCategoryFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupTabLayout()
        setupViewHandler()
    }

    private fun setupView() {
        binding.txtToolbarTitle.text = resources.getString(AppUtils.getExpenseCategory(categoryId).visibleNameResId)
    }

    private fun setupTabLayout() {
        val groupFragment = PatternCategoryGroupFragment.newInstance(expensePatternType, categoryId, currentDate.time)
        val listFragment = PatternCategoryListFragment.newInstance(expensePatternType, categoryId, currentDate.time)

        val listFragments = ArrayList<Fragment>()
        listFragments.add(groupFragment)
        listFragments.add(listFragment)

        val listFragmentTitles = ArrayList<String>()
        listFragmentTitles.add(requireContext().resources.getString(R.string.text_sub_category_group))
        listFragmentTitles.add(requireContext().resources.getString(R.string.text_sub_category_list))

        val adapter = TabAdapter(
            listFragments,
            listFragmentTitles,
            childFragmentManager
        )

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupViewHandler() {
        binding.btnBack.setOnSingleClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "PatternCategoryFragment"
        private const val PATTERN = "pattern"
        private const val CATEGORY_ID = "categoryId"
        private const val DATE = "date"

        fun newInstance(
            pattern: Int,
            categoryId: String,
            date: Long
        ) = PatternCategoryFragment().apply {
            arguments = bundleOf(PATTERN to pattern, CATEGORY_ID to categoryId, DATE to date)
        }
    }
}