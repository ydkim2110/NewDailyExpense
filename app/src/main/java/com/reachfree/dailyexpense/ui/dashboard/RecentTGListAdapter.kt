package com.reachfree.dailyexpense.ui.dashboard

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemTransactionBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 9:13
 */
class RecentTGListAdapter(
    sessionManager: SessionManager,
    private val listener: RecentTGListHeaderAdapter.OnItemClickListener
) : ListAdapter<TransactionEntity, RecentTGListAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {

            with(binding) {
                if (transaction.type == Constants.TYPE.EXPENSE.ordinal) {
                    val expenseCategory = AppUtils.getExpenseCategory(transaction.categoryId)
                    val expenseSubCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

                    imgIcon.setImageResource(expenseSubCategory.iconResId)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, expenseCategory.backgroundColor))

                    viewPatternColor.visibility = View.VISIBLE

                    when (transaction.pattern) {
                        Constants.PATTERN.NORMAL.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_normal)
                        }
                        Constants.PATTERN.WASTE.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_waste)
                        }
                        Constants.PATTERN.INVEST.ordinal -> {
                            viewPatternColor.setBackgroundResource(R.drawable.bg_pattern_invest)
                        }
                    }
                    txtDescription.text = transaction.description
                    txtCategory.setText(expenseCategory.visibleNameResId)
                    txtSubCategory.setText(expenseSubCategory.visibleNameResId)

                    //TODO: 화폐단위
                    transaction.amount?.let {
                        txtAmount.text = AppUtils.changeAmountByCurrency(it)
                    }
                    txtAmount.setTextColor(ContextCompat.getColor(root.context, R.color.colorExpense))
                }
                else if (transaction.type == Constants.TYPE.INCOME.ordinal) {
                    val incomeCategory = AppUtils.getIncomeCategory(transaction.categoryId)

                    imgIcon.setImageResource(incomeCategory.iconResId)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, incomeCategory.backgroundColor))

                    txtDescription.text = transaction.description
                    txtCategory.setText(incomeCategory.visibleNameResId)
                    txtSubCategory.visibility = View.INVISIBLE

                    //TODO: 화폐단위
                    transaction.amount?.let {
                        txtAmount.text = AppUtils.changeAmountByCurrency(it)
                    }
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