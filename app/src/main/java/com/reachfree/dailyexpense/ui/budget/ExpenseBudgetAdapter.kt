package com.reachfree.dailyexpense.ui.budget

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.ExpenseByCategoryWithBudget
import com.reachfree.dailyexpense.databinding.*
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.ANIMATION_DURATION
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import timber.log.Timber
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class ExpenseBudgetAdapter(
    private val daysOfMonth: Int
) : ListAdapter<ExpenseByCategoryWithBudget, ExpenseBudgetAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemExpenseBudgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expenseByCategory: ExpenseByCategoryWithBudget) {
            val category = AppUtils.getExpenseCategory(expenseByCategory.categoryId)
            val spentAmount = expenseByCategory.sumByCategory
            val budgetedAmount = expenseByCategory.budgetAmount
            val leftToSpendAmount = spentAmount?.let { budgetedAmount?.minus(it) }

            with(binding) {
                imgCategoryIcon.setImageResource(category.iconResId)
                imgCategoryIcon.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, category.backgroundColor))

                txtCategoryName.setText(category.visibleNameResId)

                progressbarCategory.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, category.backgroundColor))

                spentAmount?.let {
                    //TODO: 단위
                    val amountPerDay = "${AppUtils.changeAmountByCurrency(AppUtils.divideBigDecimal(it, BigDecimal(daysOfMonth)))}/day"
                    txtAmountPerDay.text = amountPerDay

                    if (budgetedAmount!! > BigDecimal(0)) {
                        AppUtils.animateTextViewPercent(
                            txtPercent,
                            ANIMATION_DURATION,
                            0,
                            AppUtils.calculatePercentage(it, expenseByCategory.budgetAmount!!)
                        )
                    }

                    progressbarCategory.max = expenseByCategory.budgetAmount!!.toInt()
                    AppUtils.animateProgressbar(progressbarCategory, it.toInt())
                }

                val spentAmountText = spentAmount?.let { AppUtils.changeAmountByCurrency(it) }
                    ?: AppUtils.changeAmountByCurrency(BigDecimal(0))
                txtExpenseAmount.text = spentAmountText

                val budgetedAmountText = budgetedAmount?.let { AppUtils.changeAmountByCurrency(it) }
                    ?: AppUtils.changeAmountByCurrency(BigDecimal(0))
                txtBudgetAmount.text = budgetedAmountText

                val leftToSpendAmountText = leftToSpendAmount?.let { AppUtils.changeAmountByCurrency(it) }
                    ?: AppUtils.changeAmountByCurrency(BigDecimal(0))
                txtLeftToSpendAmount.text = leftToSpendAmountText
            }

            binding.fabDetail.setOnSingleClickListener {
                onItemClickListener?.let { it(category.id) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.size == 1) {
            0
        } else {
            if (currentList.size % 2 == 0) {
                1
            } else {
                if (position > 1 && position == currentList.size - 1) 0 else 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemExpenseBudgetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<ExpenseByCategoryWithBudget>() {
        override fun areItemsTheSame(
            oldItem: ExpenseByCategoryWithBudget,
            newItem: ExpenseByCategoryWithBudget
        ): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(
            oldItem: ExpenseByCategoryWithBudget,
            newItem: ExpenseByCategoryWithBudget
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

}