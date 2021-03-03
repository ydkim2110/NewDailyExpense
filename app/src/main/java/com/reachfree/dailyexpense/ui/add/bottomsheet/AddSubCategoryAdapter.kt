package com.reachfree.dailyexpense.ui.add.bottomsheet

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.SubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemAddCategoryBinding
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-22
 * Time: 오전 10:29
 */
class AddSubCategoryAdapter : ListAdapter<SubCategory, AddSubCategoryAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemAddCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategory: SubCategory) {
            with(binding) {
                imgIcon.setImageResource(subCategory.iconResId)
                imgIcon.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                txtCategoryName.text = root.resources.getString(subCategory.visibleNameResId)

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(subCategory) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemAddCategoryBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<SubCategory>() {
        override fun areItemsTheSame(
            oldItem: SubCategory,
            newItem: SubCategory
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SubCategory,
            newItem: SubCategory
        ): Boolean {
            return oldItem == newItem
        }

    }

    private var onItemClickListener: ((SubCategory) -> Unit)? = null

    fun setOnItemClickListener(listener: (SubCategory) -> Unit) {
        onItemClickListener = listener
    }

}