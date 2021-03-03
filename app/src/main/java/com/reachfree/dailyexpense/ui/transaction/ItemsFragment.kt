package com.reachfree.dailyexpense.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemsFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.viewmodel.TransactionViewModel
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 11:41
 */
@AndroidEntryPoint
class ItemsFragment : BaseFragment<ItemsFragmentBinding>(),
    TGListHeaderAdapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by viewModels()
    private val transactionAdapter: TGListHeaderAdapter by lazy {
        TGListHeaderAdapter(this)
    }

    private var category = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = requireArguments().getInt(CATEGORY, 0)
    }
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ItemsFragmentBinding {
        return ItemsFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.recyclerTransaction.adapter = transactionAdapter
        when (category) {
            Constants.TOTAL_TAB -> {
                Timber.d("TOTAL_TAB")
                viewModel.thisMonthTransaction.observe(viewLifecycleOwner) { data ->
                    val groupTransactions = AppUtils.groupingTransactionByDate(data)

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

                    newList.sortBy { it.key }
                    transactionAdapter.submitList(newList)
                }
            }
            Constants.EXPENSE_TAB -> {
                Timber.d("EXPENSE_TAB")
                viewModel.thisMonthExpenseTransaction.observe(viewLifecycleOwner) { data ->
                    val groupTransactions = AppUtils.groupingTransactionByDate(data)

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

                    newList.sortBy { it.key }
                    transactionAdapter.submitList(newList)
                }
            }
            Constants.INCOME_TAB -> {
                Timber.d("INCOME_TAB")
                viewModel.thisMonthInComeTransaction.observe(viewLifecycleOwner) { data ->
                    val groupTransactions = AppUtils.groupingTransactionByDate(data)

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

                    newList.sortBy { it.key }
                    transactionAdapter.submitList(newList)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    companion object {
        private const val CATEGORY = "category"

        fun newInstance(category: Int) = ItemsFragment().apply {
            arguments = bundleOf(CATEGORY to category)
        }
    }

    override fun onItemClickListener(transaction: TransactionEntity) {

    }


}