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
import timber.log.Timber
import java.util.*

class OnboardDetailFragment : BaseFragment<OnboardDetailFragmentBinding>() {

    private var position: Int = -1

    private val illustrationArray = arrayOf(
        R.drawable.intro_1,
        R.drawable.intro_2,
        R.drawable.intro_3
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

        val stringArray = arrayOf(
            getString(R.string.text_intro_1),
            getString(R.string.text_intro_2),
            getString(R.string.text_intro_3)
        )

        val text = Locale.getDefault().language

        if (Locale.getDefault().language == Locale.KOREA.language) {
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
                        ), 0, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorWasteExpense
                            )
                        ), 6, 10, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorInvestExpense
                            )
                        ), 11, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    binding.textViewMessage.setText(spannable, TextView.BufferType.SPANNABLE)
                } else {
                    binding.textViewMessage.text = stringArray[position]
                }
            }
        } else {
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
                        ), 23, 30, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorWasteExpense
                            )
                        ), 31, 37, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorInvestExpense
                            )
                        ), 38, 45, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    binding.textViewMessage.setText(spannable, TextView.BufferType.SPANNABLE)
                } else {
                    binding.textViewMessage.text = stringArray[position]
                }
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