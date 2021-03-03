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
                imgCategoryIcon.setImageResource(subCategory.iconResId)
                imgCategoryIcon.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                txtCategoryName.setText(subCategory.visibleNameResId)

                progressbarCategory.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                //TODO: 화폐단위
                subCategoryGroup.sumBySubCategory?.let {
                    txtTotalExpenseAmount.text = "${AppUtils.insertComma(it)}원"

                    progressbarCategory.max = amount.toInt()
                    AppUtils.animateProgressbar(progressbarCategory, it.toInt())
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