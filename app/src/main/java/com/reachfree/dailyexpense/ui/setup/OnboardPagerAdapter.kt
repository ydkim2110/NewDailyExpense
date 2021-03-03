package com.reachfree.dailyexpense.ui.setup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-19
 * Time: 오전 11:59
 */
class OnboardPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return OnboardDetailFragment.newInstance(position)
    }
}