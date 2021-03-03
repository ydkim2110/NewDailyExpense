package com.reachfree.dailyexpense.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemTransactionDateBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 7:49
 */
class TGListHeaderAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<TransactionGroup, TGListHeaderAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemTransactionDateBinding,
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

                //TODO: 화폐단위
                txtTotalAmountByDate.text = AppUtils.changeAmountByCurrency(total)
                
                txtDate.text = AppUtils.dayDateFormat.format(AppUtils.stringToDate(transactionGroup.key))
                txtDayOfWeek.text = AppUtils.dayOfWeekDateFormat.format(AppUtils.stringToDate(transactionGroup.key))
                txtMonthYear.text = AppUtils.monthYearDateFormat.format(AppUtils.stringToDate(transactionGroup.key))

                recyclerTransactionDate.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(root.context)
                    val listAdapter = TGListAdapter(listener)
                    adapter = listAdapter
                    listAdapter.submitList(transactionGroup.transactionList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemTransactionDateBinding.inflate(
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