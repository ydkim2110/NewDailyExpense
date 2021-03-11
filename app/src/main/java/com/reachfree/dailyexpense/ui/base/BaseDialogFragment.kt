package com.reachfree.dailyexpense.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.reachfree.dailyexpense.R

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오전 10:25
 */
abstract class BaseDialogFragment<VB: ViewBinding>: DialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimationUpAndDown
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getDialogFragmentBinding(inflater, container)
        return binding.root
    }

    abstract fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}