package com.reachfree.dailyexpense.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.SelectTypeBottomSheetBinding
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 9:51
 */
class SelectTypeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: SelectTypeBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var onSelectTypeListener: OnSelectTypeListener

    interface OnSelectTypeListener {
        fun onSelected(isExpense: Boolean)
    }

    fun setSelectTypeListener(onSelectTypeListener: OnSelectTypeListener) {
        this.onSelectTypeListener = onSelectTypeListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SelectTypeBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutExpense.setOnSingleClickListener {
            onSelectTypeListener.onSelected(true)
            dismiss()
        }
        binding.layoutIncome.setOnSingleClickListener {
            onSelectTypeListener.onSelected(false)
            dismiss()
        }
        binding.imgCloseIcon.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SelectTypeBottomSheet"
    }
}