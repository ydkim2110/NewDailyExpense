package com.reachfree.dailyexpense.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemRecentTransactionDateBinding
import com.reachfree.dailyexpense.manager.SessionManager
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 7:49
 */
class RecentTGListHeaderAdapter(
    private val sessionManager: SessionManager,
    private val listener: OnItemClickListener
): ListAdapter<TransactionGroup, RecentTGListHeaderAdapter.MyViewHolder>(DiffUtils()) {

    private val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
    val startDate = startOfDay.minusDays(1).toMillis()!!
    val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toMillis()!!

    inner class MyViewHolder(
        private val binding: ItemRecentTransactionDateBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionGroup: TransactionGroup) {
            with(binding) {
                var total = BigDecimal(0)

                val totalExpense = transactionGroup.transactionList
                    .filter { it.type == Constants.TYPE.EXPENSE.ordinal }
                    .sumOf { it.amount!! }
                val totalIncome = transactionGroup.transactionList
                    .filter { it.type == Constants.TYPE.INCOME.ordinal }
                    .sumOf { it.amount!! }

                if (totalExpense > totalIncome) {
                    total = totalExpense - totalIncome
                    txtTotalAmountByDate.setTextColor(ContextCompat.getColor(root.context, R.color.colorExpense))
                } else {
                    total = totalIncome - totalExpense
                    txtTotalAmountByDate.setTextColor(ContextCompat.getColor(root.context, R.color.colorIncome))
                }

                val stringStartDate = AppUtils.defaultDateFormat.format(startDate)
                val stringEndDate = AppUtils.defaultDateFormat.format(endDate)

                if (transactionGroup.key.equals(stringStartDate)) {
                    txtDateTittle.text = root.context.getString(R.string.yesterday)
                } else if (transactionGroup.key.equals(stringEndDate)) {
                    txtDateTittle.text = root.context.getString(R.string.today)
                } else {
                    txtDateTittle.text = transactionGroup.key
                }

                //TODO: 화폐단위
                txtTotalAmountByDate.text = CurrencyUtils.changeAmountByCurrency(total)

                recyclerRecentTransaction.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(root.context)
                    val listAdapter = RecentTGListAdapter(listener)
                    adapter = listAdapter
                    listAdapter.submitList(transactionGroup.transactionList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemRecentTransactionDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<TransactionGroup>() {
        override fun areItemsTheSame(
            oldItem: TransactionGroup,
            newItem: TransactionGroup
        ): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(
            oldItem: TransactionGroup,
            newItem: TransactionGroup
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    interface OnItemClickListener {
        fun onItemClickListener(transaction: TransactionEntity)
    }
}