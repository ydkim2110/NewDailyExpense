package com.reachfree.dailyexpense.ui.calendar.bottomsheet

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemCalFooterTransactionBinding
import com.reachfree.dailyexpense.databinding.ItemCalTransactionBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import timber.log.Timber

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-21
 * Time: 오후 8:54
 */
class TransactionListBottomSheetAdapter :
    ListAdapter<TransactionEntity, RecyclerView.ViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemCalTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {
            with(binding) {
                if (transaction.type == Constants.TYPE.EXPENSE.ordinal) {
                    val expenseCategory = AppUtils.getExpenseCategory(transaction.categoryId)
                    val expenseSubCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

                    imgIcon.setImageResource(expenseSubCategory.iconResId)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, expenseCategory.backgroundColor))
                    txtDescription.text = transaction.description
                    txtCategory.setText(expenseCategory.visibleNameResId)
                    txtSubCategory.setText(expenseSubCategory.visibleNameResId)

                    //TODO: 화폐단위
                    txtAmount.text = "${AppUtils.insertComma(transaction.amount!!)}원"
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
                    txtAmount.text = "${AppUtils.insertComma(transaction.amount!!)}원"
                    txtAmount.setTextColor(ContextCompat.getColor(root.context, R.color.colorIncome))
                }

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(transaction) }
                }
            }
        }
    }

    inner class FooterViewHolder(
        private val binding: ItemCalFooterTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnTransactionToggleGroup.addOnButtonCheckedListener { group, checkedId, _ ->
                if (group.checkedButtonId == -1) group.check(checkedId)
            }
            binding.btnExpense.setOnSingleClickListener {
                onTypeClickListener?.let { it(true) }
            }
            binding.btnIncome.setOnSingleClickListener {
                onTypeClickListener?.let { it(false) }
            }
        }
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return count + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            currentList.size -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            MyViewHolder(ItemCalTransactionBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
        } else {
            FooterViewHolder(ItemCalFooterTransactionBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                try {
                    holder.bind(getItem(position))
                } catch (e: Exception) {
                    Timber.d("ERROR: $e")
                }
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
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
            return oldItem == newItem
        }

    }

    private var onItemClickListener: ((TransactionEntity) -> Unit)? = null
    private var onTypeClickListener: ((Boolean) -> Unit)? = null

    fun setOnItemClickListener(listener: (TransactionEntity) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnTypeClickListener(listener: (Boolean) -> Unit) {
        onTypeClickListener = listener
    }

    companion object {
        private const val TYPE_ITEM= 0
        private const val TYPE_FOOTER = 1
    }


}