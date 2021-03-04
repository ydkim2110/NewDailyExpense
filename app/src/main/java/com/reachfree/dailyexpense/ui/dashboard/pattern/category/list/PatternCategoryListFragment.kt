package com.reachfree.dailyexpense.ui.dashboard.pattern.category.list

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.PatternCategoryListFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.SortType
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PatternCategoryListFragment : BaseFragment<PatternCategoryListFragmentBinding>() {

    private var expensePatternType = Constants.EXPENSE_PATTERN_TOTAL
    private var categoryId = Constants.FOOD_DRINKS

    private var isOptionsExpanded = false
    private var selectedIndex = 0
    private lateinit var filterArray: Array<String>
    private lateinit var currentDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filterArray = resources.getStringArray(R.array.filter_sort_options)
        expensePatternType = requireArguments().getInt(PATTERN, Constants.EXPENSE_PATTERN_TOTAL)
        categoryId = requireArguments().getString(CATEGORY_ID, Constants.FOOD_DRINKS)
        currentDate = Date(requireArguments().getLong(DATE, Date().time))
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PatternCategoryListFragmentBinding {
        return PatternCategoryListFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val fragment = PatternCategoryListNotGroupFragment.newInstance(expensePatternType, categoryId, currentDate.time)
            childFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutTransactionList, fragment)
                .commit()
        }

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

        binding.txtSwitcherSortBy.setText(filterArray[selectedIndex])

        binding.txtSwitcherSortBy.setOnClickListener {
            val sortType: SortType
            when {
                selectedIndex > 1 -> {
                    selectedIndex = 0
                    sortType = SortType.AMOUNT
                }
                selectedIndex > 0 -> {
                    selectedIndex ++
                    sortType = SortType.CATEGORY
                }
                else -> {
                    selectedIndex ++
                    sortType = SortType.DATE
                }
            }

            binding.txtSwitcherSortBy.setText(filterArray[selectedIndex])
            childFragmentManager.setFragmentResult("sortType", Bundle().apply { putSerializable("data", sortType) })
        }

        binding.btnOptions.setOnClickListener {
            isOptionsExpanded = if (isOptionsExpanded) {
                ObjectAnimator.ofFloat(binding.imageViewOptions, ROTATION, 0f)
                    .setDuration(ROTATION_ANIM_DURATION)
                    .start()
                binding.linearLayoutOptions.visibility = View.GONE
                false
            } else {
                ObjectAnimator.ofFloat(binding.imageViewOptions, ROTATION, 180f)
                    .setDuration(ROTATION_ANIM_DURATION)
                    .start()
                binding.linearLayoutOptions.visibility = View.VISIBLE
                true
            }
        }

        binding.switchGroupByCategory.setOnCheckedChangeListener { _, isChecked ->
            selectedIndex = 0
            binding.txtSwitcherSortBy.setText(filterArray[selectedIndex])

            if (isChecked) {
                val fragment = PatternCategoryListGroupFragment.newInstance(expensePatternType, categoryId, currentDate.time)

                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutTransactionList, fragment)
                    .commit()
            } else {
                val fragment = PatternCategoryListNotGroupFragment.newInstance(expensePatternType, categoryId, currentDate.time)

                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutTransactionList, fragment)
                    .commit()
            }
        }
    }

    companion object {
        private const val ROTATION = "rotation"
        private const val ROTATION_ANIM_DURATION = 250L

        private const val PATTERN = "pattern"
        private const val CATEGORY_ID = "categoryId"
        private const val DATE = "date"

        fun newInstance(
            pattern: Int,
            categoryId: String,
            date: Long
        ) = PatternCategoryListFragment().apply {
            arguments = bundleOf(PATTERN to pattern, CATEGORY_ID to categoryId, DATE to date)
        }
    }
}