package com.reachfree.dailyexpense.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.dailyexpense.R

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-28
 * Time: 오후 2:18
 */
class ListPaddingDecoration(
    context: Context,
    val paddingLeft: Int,
    val paddingRight: Int
) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = null

    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.recyclerview_divider)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val left = parent.paddingLeft + paddingLeft
        val right = parent.width - parent.paddingRight - paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + (mDivider?.intrinsicHeight ?: 0)

            mDivider?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }

        }
    }

}