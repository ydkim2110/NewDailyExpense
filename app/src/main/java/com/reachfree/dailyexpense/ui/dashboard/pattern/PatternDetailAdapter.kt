package com.reachfree.dailyexpense.ui.dashboard.pattern

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.ExpenseByCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemCategoryBinding
import com.reachfree.dailyexpense.databinding.ItemExpenseByCategoryBinding
import com.reachfree.dailyexpense.databinding.ItemTransactionDateBinding
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import java.math.BigDecimal
import kotlin.math.exp

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class PatternDetailAdapter : ListAdapter<ExpenseByCategory, PatternDetailAdapter.MyViewHolder>(DiffUtils()) {

    private lateinit var amount: BigDecimal
    private lateinit var total: BigDecimal

    override fun onCurrentListChanged(
        previousList: MutableList<ExpenseByCategory>,
        currentList: MutableList<ExpenseByCategory>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        if (currentList.size > 0) {
            amount = currentList.maxOf { it.sumByCategory!! }
            total = currentList.sumOf { it.sumByCategory!! }
        }
    }

    inner class MyViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expenseByCategory: ExpenseByCategory) {
            val category = AppUtils.getExpenseCategory(expenseByCategory.categoryId)

            with(binding) {
                layoutCategoryPercent.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, category.backgroundColor))

                txtCategoryName.setText(category.visibleNameResId)

                progressbarCategory.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, category.backgroundColor))

                //TODO: 화폐단위
                expenseByCategory.sumByCategory?.let {
                    val percent = it.divide(total, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal(100))

                    txtCategoryPercent.text = "${percent.toInt()}%"
                    txtTotalExpenseAmount.text = "${AppUtils.insertComma(it)}원"

                    progressbarCategory.max = amount.toInt()
                    AppUtils.animateProgressbar(progressbarCategory, it.toInt())

                    if (percent.toInt() != 100) {
                        txtCategoryPercent.textSize = 15f
                    }
                }
            }

            binding.root.setOnSingleClickListener {
                onItemClickListener?.let { it(category.id) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<ExpenseByCategory>() {
        override fun areItemsTheSame(
            oldItem: ExpenseByCategory,
            newItem: ExpenseByCategory
        ): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(
            oldItem: ExpenseByCategory,
            newItem: ExpenseByCategory
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

}