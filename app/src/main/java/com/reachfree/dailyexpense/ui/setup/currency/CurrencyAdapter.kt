package com.reachfree.dailyexpense.ui.setup.currency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.databinding.ItemCurrencyBinding
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-25
 * Time: 오전 10:01
 */
class CurrencyAdapter : ListAdapter<Currency, CurrencyAdapter.MyViewHolder>(DiffUtils()) {

    inner class MyViewHolder(
        private val binding: ItemCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind (currency: Currency) {
            with(binding) {
                txtFlag.text = currency.flag
                txtCode.text = currency.code
                txtTitle.text = currency.title

                root.setOnSingleClickListener {
                    onItemClickListener?.let { it(currency) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtils : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(
            oldItem: Currency,
            newItem: Currency
        ): Boolean {
            return oldItem.flag == newItem.flag
        }

        override fun areContentsTheSame(
            oldItem: Currency,
            newItem: Currency
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var onItemClickListener: ((Currency) -> Unit)? = null

    fun setOnItemClickListener(listener: (Currency) -> Unit) {
        onItemClickListener = listener
    }

}