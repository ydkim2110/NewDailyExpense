package com.reachfree.dailyexpense.ui.dashboard.pattern.category.list

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.ItemExpenseByCategoryBinding
import com.reachfree.dailyexpense.databinding.ItemPatternCategoryListBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import timber.log.Timber
import java.math.BigDecimal

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 6:29
 */
class PatternCategoryListAdapter(
    private val list: List<TransactionEntity>
) : RecyclerView.Adapter<PatternCategoryListAdapter.MyViewHolder>() {

    private var amount: BigDecimal = list.maxOf { it.amount ?: BigDecimal(0) }

    inner class MyViewHolder(
        private val binding: ItemPatternCategoryListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {
            val subCategory = AppUtils.getExpenseSubCategory(transaction.subCategoryId)

            with(binding) {
                txtDate.text = AppUtils.dayDateFormat.format(transaction.registerDate)
                txtCategoryName.text = transaction.description

                txtSubcategoryName.text = root.resources.getString(subCategory.visibleNameResId)

                progressbarCategory.progressTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, subCategory.backgroundColor))

                //TODO: 화폐단위
                transaction.amount?.let {
                    txtTotalExpenseAmount.text = "${AppUtils.insertComma(transaction.amount!!)}원"

                    progressbarCategory.max = amount.toInt()
                    AppUtils.animateProgressbar(progressbarCategory, it.toInt())
                }

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(transaction) }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemPatternCategoryListBinding.inflate(
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

    private var onItemClickListener: ((TransactionEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (TransactionEntity) -> Unit) {
        onItemClickListener = listener
    }
}