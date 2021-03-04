package com.reachfree.dailyexpense.ui.dashboard.pattern.category.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.PatternCategoryListGroupFragmentBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.category.PatternCategoryViewModel
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.SortType
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class PatternCategoryListGroupFragment : BaseFragment<PatternCategoryListGroupFragmentBinding>(),
    PatternCategoryListGroupHeaderAdapter.OnItemClickListener {

    private val viewModel: PatternCategoryViewModel by viewModels()

    private lateinit var listGroupAdapter: PatternCategoryListGroupHeaderAdapter

    private var startOfMonth = 0L
    private var endOfMonth = 0L
    private lateinit var currentDate: Date

    private val subCategoryIds = mutableListOf<String>()
    private val subCategoryIdArray = mutableListOf<String>()
    private val subCategoryNameArray = mutableListOf<String>()

    private var expensePatternType = Constants.EXPENSE_PATTERN_TOTAL
    private var categoryId = Constants.FOOD_DRINKS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getInt(PATTERN, NORMAL.ordinal).also { expensePatternType = it }
        requireArguments().getString(CATEGORY_ID, Constants.FOOD_DRINKS).also { categoryId = it }
        requireArguments().getLong(DATE, Date().time).also { currentDate = Date(it) }

        startOfMonth = AppUtils.startOfMonth(AppUtils.convertDateToYearMonth(currentDate))
        endOfMonth = AppUtils.endOfMonth(AppUtils.convertDateToYearMonth(currentDate))

        Timber.d("start ${AppUtils.defaultDateFormat.format(startOfMonth)}")
        Timber.d("endOfMonth ${AppUtils.defaultDateFormat.format(endOfMonth)}")

        setFragmentResultListener("sortType") { _, bundle ->
            val sortType = bundle.getSerializable("data") as SortType
            when (expensePatternType) {
                Constants.EXPENSE_PATTERN_TOTAL -> {
                    viewModel.getTransactionBySubCategorySortedBy(
                        sortType,
                        startOfMonth,
                        endOfMonth,
                        intArrayOf(NORMAL.ordinal, WASTE.ordinal, INVEST.ordinal),
                        categoryId
                    )
                }
                Constants.EXPENSE_PATTERN_NORMAL -> {
                    viewModel.getTransactionBySubCategorySortedBy(
                        sortType,
                        startOfMonth,
                        endOfMonth,
                        intArrayOf(NORMAL.ordinal),
                        categoryId
                    )
                }
                Constants.EXPENSE_PATTERN_WASTE -> {
                    viewModel.getTransactionBySubCategorySortedBy(
                        sortType,
                        startOfMonth,
                        endOfMonth,
                        intArrayOf(WASTE.ordinal),
                        categoryId
                    )
                }
                Constants.EXPENSE_PATTERN_INVEST -> {
                    viewModel.getTransactionBySubCategorySortedBy(
                        sortType,
                        startOfMonth,
                        endOfMonth,
                        intArrayOf(INVEST.ordinal),
                        categoryId
                    )
                }
            }

        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PatternCategoryListGroupFragmentBinding {
        return PatternCategoryListGroupFragmentBinding.inflate(inflater, container, false)
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
                viewModel.getSubCategoryGroup(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal, WASTE.ordinal, INVEST.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_NORMAL -> {
                viewModel.getSubCategoryGroup(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(NORMAL.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_WASTE -> {
                viewModel.getSubCategoryGroup(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(WASTE.ordinal),
                    categoryId
                )
            }
            Constants.EXPENSE_PATTERN_INVEST -> {
                viewModel.getSubCategoryGroup(
                    startOfMonth,
                    endOfMonth,
                    intArrayOf(INVEST.ordinal),
                    categoryId
                )
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerCategoryGroup.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObserver() {
        viewModel.thisMonthSubCategoryGroup.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Constants.Status.LOADING -> {

                }
                Constants.Status.SUCCESS -> {
                    subCategoryIds.clear()
                    subCategoryIdArray.clear()
                    subCategoryNameArray.clear()

                    subCategoryIdArray.add(SHOW_ALL)
                    subCategoryNameArray.add(resources.getString(R.string.text_all))

                    for (i in result.data!!.indices) {
                        subCategoryIds.add(AppUtils.getExpenseSubCategory(result.data[i].subCategoryId).id)
                        subCategoryIdArray.add(AppUtils.getExpenseSubCategory(result.data[i].subCategoryId).id)
                        subCategoryNameArray.add(resources.getString(AppUtils.getExpenseSubCategory(result.data[i].subCategoryId).visibleNameResId))
                    }

                    when (expensePatternType) {
                        Constants.EXPENSE_PATTERN_TOTAL -> {
                            getTransaction(intArrayOf(NORMAL.ordinal, WASTE.ordinal, INVEST.ordinal))
                        }
                        Constants.EXPENSE_PATTERN_NORMAL -> {
                            getTransaction(intArrayOf(NORMAL.ordinal))
                        }
                        Constants.EXPENSE_PATTERN_WASTE -> {
                            getTransaction(intArrayOf(WASTE.ordinal))
                        }
                        Constants.EXPENSE_PATTERN_INVEST -> {
                            getTransaction(intArrayOf(INVEST.ordinal))
                        }
                    }

                }
                Constants.Status.ERROR -> {

                }
            }
        }

        viewModel.subCategorySortBy.observe(viewLifecycleOwner) { result ->
            val groupTransactions = AppUtils.groupingTransactionBySubCategory(result)

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

            listGroupAdapter = PatternCategoryListGroupHeaderAdapter(newList, this)
            binding.recyclerCategoryGroup.adapter = listGroupAdapter
        }

    }

    private fun getTransaction(patterns: IntArray) {
        viewModel.getTransactionBySubCategorySortedBy(
            SortType.AMOUNT,
            startOfMonth,
            endOfMonth,
            patterns,
            categoryId
        )
    }

    override fun onItemClickListener(transaction: TransactionEntity) {
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

    companion object {
        const val TAG = "PatternCategoryListGroupFragment"

        private const val SHOW_ALL = "show_all"
        private const val PATTERN = "pattern"
        private const val CATEGORY_ID = "categoryId"
        private const val DATE = "date"

        fun newInstance(pattern: Int, categoryId: String, date: Long) = PatternCategoryListGroupFragment().apply {
            arguments = bundleOf(PATTERN to pattern, CATEGORY_ID to categoryId, DATE to date)
        }
    }

}