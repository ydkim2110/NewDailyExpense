package com.reachfree.dailyexpense.ui.setup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.OnboardFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

class OnboardFragment : BaseFragment<OnboardFragmentBinding>() {

    private lateinit var onSetupFinished: OnSetupFinished

    private var currentPosition = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is SetupActivity) {
            onSetupFinished = activity as SetupActivity
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): OnboardFragmentBinding {
        return OnboardFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setupViewPager.adapter = OnboardPagerAdapter(childFragmentManager)
        binding.setupTabLayout.setupWithViewPager(binding.setupViewPager)

        updateButtonText(currentPosition)

        binding.setupViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                updateButtonText(position)
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        binding.btnDoneSkip.setOnSingleClickListener {
            if (currentPosition == 2) {
                if (activity is SetupActivity) {
                    (activity as SetupActivity).onSetupFinished()
                }
            } else {
                binding.setupViewPager.setCurrentItem(2, true)
            }
        }
    }

    private fun updateButtonText(currentPosition: Int) {
        if (currentPosition == 2) {
            binding.btnDoneSkip.text = getString(R.string.text_done)
        } else {
            binding.btnDoneSkip.text = getString(R.string.text_skip)
        }
    }

    interface OnSetupFinished {
        fun onSetupFinished()
    }

    companion object {
        fun newInstance() = OnboardFragment()
    }

}