package com.reachfree.dailyexpense.ui.add.bottomsheet

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemAddCategoryBinding
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-22
 * Time: 오전 10:29
 */
class AddCategoryAdapter : ListAdapter<Category, AddCategoryAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemAddCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            with(binding) {
                imgIcon.setImageResource(category.iconResId)
                imgIcon.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, category.backgroundColor))

                txtCategoryName.text = root.resources.getString(category.visibleNameResId)

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(category) }
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

    private class DiffUtils : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(
            oldItem: Category,
            newItem: Category
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Category,
            newItem: Category
        ): Boolean {
            return oldItem == newItem
        }

    }

    private var onItemClickListener: ((Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }

}