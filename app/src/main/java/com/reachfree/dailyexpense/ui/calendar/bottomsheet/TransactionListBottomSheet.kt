package com.reachfree.dailyexpense.ui.calendar.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.TransactionListBottomSheetBinding
import com.reachfree.dailyexpense.ui.add.AddExpenseFragment
import com.reachfree.dailyexpense.ui.add.AddIncomeFragment
import com.reachfree.dailyexpense.ui.base.BaseBottomSheetDialogFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants.TYPE
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.getQuantityString
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 9:51
 */
@AndroidEntryPoint
class TransactionListBottomSheet(
    private val date: Date
) : BaseBottomSheetDialogFragment<TransactionListBottomSheetBinding>() {

    private val viewModel: TransactionListBottomSheetViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionListBottomSheetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SelectTypeBottomSheet)
    }

    override fun getBottomSheetDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TransactionListBottomSheetBinding {
        return TransactionListBottomSheetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupView() {
        binding.txtSelectedDate.text = AppUtils.addTransactionDateFormat.format(date)

        viewModel.date.value = listOf(
            AppUtils.calculateStartOfDay(AppUtils.convertDateToLocalDate(date)).toMillis()!!,
            AppUtils.calculateEndOfDay(AppUtils.convertDateToLocalDate(date)).toMillis()!!
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewHandler() {
        binding.imgCloseIcon.setOnSingleClickListener {
            dismiss()
        }
    }

    private fun subscribeToObserver() {
        viewModel.transactionList.observe(this) { response ->
            response?.let {
                binding.txtTotalAmountTitle.text = requireContext().getQuantityString(
                    response.count(),
                    R.plurals.number_of_transaction_count,
                    R.string.text_transaction_count_zero
                )

                val expenseTotal = response
                    .filter { it.type == EXPENSE.ordinal }
                    .sumOf { it.amount!! }

                val incomeTotal = response
                    .filter { it.type == INCOME.ordinal }
                    .sumOf { it.amount!! }

                val totalAmount = if (expenseTotal > incomeTotal) {
                    expenseTotal.minus(incomeTotal)
                } else {
                    incomeTotal.minus(expenseTotal)
                }

                binding.txtTotalAmount.text = CurrencyUtils.changeAmountByCurrency(totalAmount)
            }

            transactionAdapter = TransactionListBottomSheetAdapter()
            binding.recyclerTransaction.adapter = transactionAdapter
            transactionAdapter.submitList(response)

            transactionAdapter.setOnItemClickListener { transaction ->
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

            transactionAdapter.setOnTypeClickListener { isExpense ->
                when (isExpense) {
                    true -> {
                        val addExpenseFragment = AddExpenseFragment.newInstance(dateLong = date.time)
                        addExpenseFragment.show(childFragmentManager, null)
                    }
                    false -> {
                        val addIncomeFragment = AddIncomeFragment.newInstance()
                        addIncomeFragment.show(childFragmentManager, null)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "TransactionListBottomSheet"
    }

}