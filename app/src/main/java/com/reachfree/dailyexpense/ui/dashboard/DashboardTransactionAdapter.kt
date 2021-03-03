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
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemTransactionBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-16
 * Time: 오후 1:36
 */
class DashboardTransactionAdapter : ListAdapter<TransactionEntity, DashboardTransactionAdapter.MyViewHolder>(DiffUtils()) {

    class MyViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionEntity) {

            with(binding) {
                if (transaction.type == Constants.TYPE.EXPENSE.ordinal) {
                    val expenseCategory = AppUtils.getExpenseCategory(transaction.categoryId)
                    val expenseSubCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

                    imgIcon.setImageResource(expenseSubCategory.iconResId)
                    imgIcon.backgroundTintList = ColorStateList.valueOf(expenseCategory.backgroundColor)
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
                    imgIcon.backgroundTintList = ColorStateList.valueOf(incomeCategory.backgroundColor)
                    txtDescription.text = transaction.description
                    txtCategory.setText(incomeCategory.visibleNameResId)
                    txtSubCategory.visibility = View.INVISIBLE

                    //TODO: 화폐단위
                    txtAmount.text = "${AppUtils.insertComma(transaction.amount!!)}원"
                    txtAmount.setTextColor(ContextCompat.getColor(root.context, R.color.colorIncome))
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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