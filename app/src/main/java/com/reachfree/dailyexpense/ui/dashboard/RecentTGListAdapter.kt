package com.reachfree.dailyexpense.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemTransactionBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE.INCOME
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.changeBackgroundTintColor
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 9:13
 */
class RecentTGListAdapter(
    private val listener: RecentTGListHeaderAdapter.OnItemClickListener
) : ListAdapter<TransactionEntity, RecentTGListAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {

            with(binding) {
                if (transaction.type == EXPENSE.ordinal) {
                    val expenseCategory = AppUtils.getExpenseCategory(transaction.categoryId)
                    val expenseSubCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

                    imgIcon.load(expenseSubCategory.iconResId)
                    imgIcon.changeBackgroundTintColor(expenseCategory.backgroundColor)

                    viewPatternColor.visibility = View.VISIBLE

                    when (transaction.pattern) {
                        NORMAL.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_normal)
                        }
                        WASTE.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_waste)
                        }
                        INVEST.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_invest)
                        }
                    }

                    txtDescription.text = transaction.description
                    txtCategory.setText(expenseCategory.visibleNameResId)
                    txtSubCategory.setText(expenseSubCategory.visibleNameResId)

                    txtAmount.text = transaction.amount?.let { CurrencyUtils.changeAmountByCurrency(it) }
                        ?: CurrencyUtils.changeAmountByCurrency(BigDecimal(0))
                    txtAmount.setTextColor(ContextCompat.getColor(root.context, R.color.colorExpense))
                }
                else if (transaction.type == INCOME.ordinal) {
                    val incomeCategory = AppUtils.getIncomeCategory(transaction.categoryId)

                    imgIcon.load(incomeCategory.iconResId)
                    imgIcon.changeBackgroundTintColor(incomeCategory.backgroundColor)

                    txtDescription.text = transaction.description
                    txtCategory.setText(incomeCategory.visibleNameResId)
                    txtSubCategory.visibility = View.INVISIBLE

                    txtAmount.text = transaction.amount?.let { CurrencyUtils.changeAmountByCurrency(it) }
                        ?: CurrencyUtils.changeAmountByCurrency(BigDecimal(0))
                    txtAmount.setTextColor(ContextCompat.getColor(root.context, R.color.colorIncome))
                }

                root.setOnSingleClickListener {
                    listener.onItemClickListener(transaction)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    private class DiffUtils : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
}