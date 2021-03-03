package com.reachfree.dailyexpense.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-19
 * Time: 오후 8:25
 */
class SpacesItemDecoration(
    private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = space
        outRect.bottom = space
        outRect.left = space

        if (parent.getChildLayoutPosition(view) == -1) {
            outRect.top = space
        } else {
            outRect.top = 0
        }
    }

}