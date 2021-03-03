package com.reachfree.dailyexpense.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오전 10:25
 */
abstract class BaseBottomSheetDialogFragment<VB: ViewBinding>: BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getBottomSheetDialogFragmentBinding(inflater, container)
        return binding.root
    }

    abstract fun getBottomSheetDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}