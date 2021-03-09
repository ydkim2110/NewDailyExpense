package com.reachfree.dailyexpense.ui.budget.create

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemCreateBudgetBinding
import com.reachfree.dailyexpense.databinding.ItemExpenseBudgetBinding
import com.reachfree.dailyexpense.databinding.ItemTransactionRowBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.changeImageTintColor
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import java.math.BigDecimal
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class CreateBudgetAdapter
    : ListAdapter<CreateBudgetModel, CreateBudgetAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemCreateBudgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(createBudget: CreateBudgetModel) {
            with(binding) {
                imgCategoryIcon.load(createBudget.category!!.iconResId)
                imgCategoryIcon.changeImageTintColor(createBudget.category!!.backgroundColor)

                txtCategoryName.setText(createBudget.category!!.visibleNameResId)
                txtBudgetedAmount.text =
                    CurrencyUtils.changeAmountByCurrency(createBudget.budgetedAmount ?: BigDecimal(0))
            }

            binding.root.setOnSingleClickListener {
                onItemClickListener?.let { it(createBudget.category!!.id) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCreateBudgetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<CreateBudgetModel>() {
        override fun areItemsTheSame(
            oldItem: CreateBudgetModel,
            newItem: CreateBudgetModel
        ): Boolean {
            return oldItem.category!!.id == newItem.category!!.id
        }

        override fun areContentsTheSame(
            oldItem: CreateBudgetModel,
            newItem: CreateBudgetModel
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

}