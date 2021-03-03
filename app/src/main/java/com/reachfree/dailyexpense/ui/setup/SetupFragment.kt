package com.reachfree.dailyexpense.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reachfree.dailyexpense.databinding.SetupFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

class SetupFragment : BaseFragment<SetupFragmentBinding>() {

    private lateinit var onGetStartedClickListener: OnGetStartedClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is SetupActivity) {
            onGetStartedClickListener = activity as SetupActivity
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SetupFragmentBinding {
        return SetupFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnSingleClickListener {
            onGetStartedClickListener.onGetStartedClicked()
        }
    }

    interface OnGetStartedClickListener {
        fun onGetStartedClicked()
    }
}