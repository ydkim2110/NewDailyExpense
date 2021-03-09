package com.reachfree.dailyexpense.ui.setup

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.OnboardDetailFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.util.extension.load

class OnboardDetailFragment : BaseFragment<OnboardDetailFragmentBinding>() {

    private var position: Int = -1

    private val illustrationArray = arrayOf(
        R.drawable.school_setup_illustration_1,
        R.drawable.school_setup_illustration_2,
        R.drawable.school_setup_illustration_3
    )

    private val stringArray = arrayOf(
        "Add homeworks, exams, and tasks",
        "Check your calendar",
        "See your schedule for the day"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let { position = it.getInt(POSITION, -1) }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): OnboardDetailFragmentBinding {
        return OnboardDetailFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (position != -1) {
            binding.imageViewIllustration.load(illustrationArray[position])
            if (position == 0) {
                val spannable = SpannableString(stringArray[position])

                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorNormalExpense
                        )
                    ), 4, 14, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorWasteExpense
                        )
                    ), 15, 21, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )

                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorInvestExpense
                        )
                    ), 26, 31, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )

                binding.textViewMessage.setText(spannable, TextView.BufferType.SPANNABLE)
            } else {
                binding.textViewMessage.text = stringArray[position]
            }
        }
    }

    companion object {
        private const val POSITION = "position"
        fun newInstance(position: Int) = OnboardDetailFragment().apply {
            arguments = bundleOf(POSITION to position)
        }
    }


}