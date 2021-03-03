package com.reachfree.dailyexpense.ui.dashboard.pattern.category.list

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemCategoryListGroupHeaderBinding
import com.reachfree.dailyexpense.ui.dashboard.RecentTGListAdapter
import com.reachfree.dailyexpense.ui.transaction.TransactionGroup
import com.reachfree.dailyexpense.util.AppUtils

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class PatternCategoryListGroupHeaderAdapter(
    private val list: List<TransactionGroup>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PatternCategoryListGroupHeaderAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemCategoryListGroupHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionGroup: TransactionGroup) {
            val subCategory = AppUtils.getExpenseSubCategory(transactionGroup.key)

            with(binding) {
                imgIcon.setImageResource(subCategory.iconResId)
                imgIcon.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                txtSubcategoryName.text = root.resources.getString(subCategory.visibleNameResId)

                recyclerSubcategoryList.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(root.context)
                    adapter = PatternCategoryListGroupAdapter(transactionGroup.transactionList, listener)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCategoryListGroupHeaderBinding.inflate(
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

    interface OnItemClickListener {
        fun onItemClickListener(transaction: TransactionEntity)
    }
}