package com.reachfree.dailyexpense.ui.transaction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 11:48
 */
class TabAdapter(
    private val listFragments: ArrayList<Fragment>,
    private val listFragmentTitles: ArrayList<String>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return listFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return listFragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listFragmentTitles[position]
    }

}