package com.reachfree.dailyexpense.ui.settings.backup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.DialogConfirmationBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-13
 * Time: 오후 2:32
 */
class ConfirmationDialog : BaseDialogFragment<DialogConfirmationBinding>() {

    private var withPassword = false

    private lateinit var confirmationDialogResult: ConfirmationDialogResult

    interface ConfirmationDialogResult {
        fun onConfirmationDialogFinished(result: Int, withPassword: Boolean)
    }

    fun setBackupPasswordResult(confirmationDialogResult: ConfirmationDialogResult) {
        this.confirmationDialogResult = confirmationDialogResult
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let { withPassword = it.getBoolean(WITH_PASSWORD, false) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmationBinding {
        return DialogConfirmationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val code = getRandom4DigitCode()
        binding.txtCode.text = code.toString()

        binding.btnDone.setOnSingleClickListener {
            if (binding.edtCode.text.toString() == code.toString()) {
                confirmationDialogResult.onConfirmationDialogFinished(CONFIRM, withPassword)
                dismiss()
            } else {
                binding.edtCode.error = getString(R.string.text_codes_not_match)
            }
        }

        binding.btnCancel.setOnSingleClickListener {
            confirmationDialogResult.onConfirmationDialogFinished(CANCEL, withPassword)
            dismiss()
        }
    }

    private fun getRandom4DigitCode(): Int {
        return ((Math.random() * 9000) + 1000).toInt()
    }

    companion object {
        private const val WITH_PASSWORD = "WithPassword"
        private const val CONFIRM = 0
        private const val CANCEL = 1

        fun newInstance(withPassword: Boolean) = ConfirmationDialog().apply {
            arguments = bundleOf(WITH_PASSWORD to withPassword)
        }
    }
}