package com.reachfree.dailyexpense.ui.budget.detail

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemTransactionRowBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import java.math.BigDecimal
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class ExpenseBudgetDetailAdapter
    : ListAdapter<TransactionEntity, ExpenseBudgetDetailAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemTransactionRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {
            val subCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

            with(binding) {
                imgSubCategoryIcon.setImageResource(subCategory.iconResId)
                imgSubCategoryIcon.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                txtDescription.text = transaction.description
                txtDate.text = AppUtils.defaultDateFormat.format(Date(transaction.registerDate))
                txtAmount.text = AppUtils.changeAmountByCurrency(transaction.amount ?: BigDecimal(0))
            }

            binding.root.setOnSingleClickListener {
                onItemClickListener?.let { it(transaction) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemTransactionRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
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

    private var onItemClickListener: ((TransactionEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (TransactionEntity) -> Unit) {
        onItemClickListener = listener
    }

}