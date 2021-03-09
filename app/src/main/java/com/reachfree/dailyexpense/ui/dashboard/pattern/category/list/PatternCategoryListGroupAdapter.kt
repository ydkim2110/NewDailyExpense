package com.reachfree.dailyexpense.ui.dashboard.pattern.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemPatternCategoryListGroupBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.changeTintColor
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class PatternCategoryListGroupAdapter(
    private val list: List<TransactionEntity>,
    private val listener: PatternCategoryListGroupHeaderAdapter.OnItemClickListener
) : RecyclerView.Adapter<PatternCategoryListGroupAdapter.MyViewHolder>() {

    private var amount: BigDecimal = list.maxOf { it.amount!! }

    inner class MyViewHolder(
        private val binding: ItemPatternCategoryListGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {
            val subCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

            with(binding) {
                txtDate.text = AppUtils.dayDateFormat.format(transaction.registerDate)
                txtCategoryName.text = transaction.description
                txtSubcategoryName.text = root.resources.getString(subCategory.visibleNameResId)

                progressbarCategory.changeTintColor(subCategory.backgroundColor)

                transaction.amount?.let {
                    txtTotalExpenseAmount.text = CurrencyUtils.changeAmountByCurrency(it)

                    progressbarCategory.max = amount.toInt()
                    AppUtils.animateProgressbar(progressbarCategory, it.toInt())
                }

                root.setOnSingleClickListener {
                    listener.onItemClickListener(transaction)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemPatternCategoryListGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}