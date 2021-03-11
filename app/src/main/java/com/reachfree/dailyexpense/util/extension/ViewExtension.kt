package com.reachfree.dailyexpense.util.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.reachfree.dailyexpense.util.OnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오후 1:59
 */
fun View.setOnSingleClickListener(action: (View) -> Unit) {
    val onClick = OnSingleClickListener {
        action(it)
    }
    setOnClickListener(onClick)
}