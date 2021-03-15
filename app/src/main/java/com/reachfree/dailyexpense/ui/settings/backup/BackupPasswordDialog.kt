package com.reachfree.dailyexpense.ui.settings.backup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.DialogBackupPasswordBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-13
 * Time: 오후 2:32
 */
class BackupPasswordDialog : BaseDialogFragment<DialogBackupPasswordBinding>() {

    private var password: String? = null
    private var isPasswordEnabled = false

    private lateinit var backupPasswordResult: BackupPasswordResult

    interface BackupPasswordResult {
        fun onBackupPasswordFinished(isPasswordEnabled: Boolean, password: String?)
    }

    fun setBackupPasswordResult(backupPasswordResult: BackupPasswordResult) {
        this.backupPasswordResult = backupPasswordResult
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
    ): DialogBackupPasswordBinding {
        return DialogBackupPasswordBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chbPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPassword.error = null
            binding.edtPassword.isEnabled = isChecked
        }

        binding.btnBackup.setOnSingleClickListener {
            val error = if (binding.chbPassword.isChecked) {
                validateBackupPasswordInput(binding.edtPassword.text.toString())
            } else {
                null
            }

            if (binding.chbPassword.isChecked) {
                binding.edtPassword.error = getStringError(error)
            }

            if (error != null) {
                return@setOnSingleClickListener
            }

            isPasswordEnabled = binding.chbPassword.isChecked
            password = if (isPasswordEnabled) binding.edtPassword.text.toString() else null
            backupPasswordResult.onBackupPasswordFinished(isPasswordEnabled, password)
            dismiss()
        }

        binding.btnCancel.setOnSingleClickListener { dismiss() }
    }

    private fun validateBackupPasswordInput(password: String): Int? {
        return if (password.isNotEmpty()) {
            if (password.trim() == "") {
                BLANK_PASSWORD
            } else {
                null
            }
        } else {
            EMPTY_PASSWORD
        }
    }

    private fun getStringError(error: Int?): CharSequence? {
        return when (error) {
            EMPTY_PASSWORD -> {
                getString(R.string.this_field_is_required)
            }
            BLANK_PASSWORD -> {
                getString(R.string.password_cannot_be_blank)
            }
            else -> {
                null
            }
        }
    }

    companion object {
        private const val EMPTY_PASSWORD = 0
        private const val BLANK_PASSWORD = 1

        fun newInstance() = BackupPasswordDialog()
    }
}