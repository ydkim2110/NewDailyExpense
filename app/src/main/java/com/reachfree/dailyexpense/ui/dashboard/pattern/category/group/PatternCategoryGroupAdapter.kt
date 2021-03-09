package com.reachfree.dailyexpense.ui.dashboard.pattern.category.group

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.ExpenseBySubCategory
import com.reachfree.dailyexpense.databinding.ItemExpenseByCategoryBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.animateProgressbar
import com.reachfree.dailyexpense.util.extension.changeBackgroundTintColor
import com.reachfree.dailyexpense.util.extension.changeTintColor
import com.reachfree.dailyexpense.util.extension.load
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class PatternCategoryGroupAdapter
    : ListAdapter<ExpenseBySubCategory, PatternCategoryGroupAdapter.MyViewHolder>(DiffUtils()) {

    private lateinit var amount: BigDecimal

    override fun onCurrentListChanged(
        previousList: MutableList<ExpenseBySubCategory>,
        currentList: MutableList<ExpenseBySubCategory>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        if (currentList.size > 0) {
            amount = currentList.maxOf { it.sumBySubCategory ?: BigDecimal(0) }
        }
    }

    inner class MyViewHolder(
        private val binding: ItemExpenseByCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategoryGroup: ExpenseBySubCategory) {
            val subCategory = AppUtils.getExpenseSubCategory(subCategoryGroup.subCategoryId)

            with(binding) {
                imgCategoryIcon.load(subCategory.iconResId)
                imgCategoryIcon.changeBackgroundTintColor(subCategory.backgroundColor)

                txtCategoryName.setText(subCategory.visibleNameResId)

                progressbarCategory.changeTintColor(subCategory.backgroundColor)

                subCategoryGroup.sumBySubCategory?.let {
                    txtTotalExpenseAmount.text = CurrencyUtils.changeAmountByCurrency(it)

                    progressbarCategory.max = amount.toInt()
                    progressbarCategory.animateProgressbar(it.toInt())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemExpenseByCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<ExpenseBySubCategory>() {
        override fun areItemsTheSame(
            oldItem: ExpenseBySubCategory,
            newItem: ExpenseBySubCategory
        ): Boolean {
            return oldItem.subCategoryId == newItem.subCategoryId
        }

        override fun areContentsTheSame(
            oldItem: ExpenseBySubCategory,
            newItem: ExpenseBySubCategory
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

}