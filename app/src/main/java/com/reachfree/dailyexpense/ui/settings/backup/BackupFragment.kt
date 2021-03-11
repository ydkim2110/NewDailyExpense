package com.reachfree.dailyexpense.ui.settings.backup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.databinding.BackupFragmentBinding
import com.reachfree.dailyexpense.databinding.ProfileFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.ProgressBarDialog
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BackupFragment : BaseDialogFragment<BackupFragmentBinding>() {

    @Inject lateinit var sessionManager: SessionManager

    private var password: String? = null
    private var isPasswordEnabled = false

    private lateinit var progressBarDialog: ProgressBarDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        progressBarDialog = ProgressBarDialog(requireContext())
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BackupFragmentBinding {
        return BackupFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupViewHandler()
    }

    private fun setupToolbar() {
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
        binding.appBar.txtToolbarTitle.text = "Backup and Restore"
    }

    private fun setupViewHandler() {
        binding.btnCreateBackup.setOnSingleClickListener {
            showBackupPasswordDialog()
        }
        binding.btnRestore.setOnSingleClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == BACKUP_REQUEST_CODE) {
                showProgressBar()
                backupLocal(data)
            }
        }
    }

    private fun backupLocal(data: Intent) {
        lifecycleScope.launch(Dispatchers.IO) {
            val uri = data.data!!
            val pfd = requireContext().contentResolver.openFileDescriptor(uri, "w")
            pfd?.use {
                FileOutputStream(pfd.fileDescriptor).use {

                }
            }

        }
    }

    private fun showProgressBar() {
        progressBarDialog.show()
    }

    private fun hideProgressBar() {
        progressBarDialog.dismiss()
    }

    private fun showBackupPasswordDialog() {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_backup_password_dialog, LinearLayout(requireContext()), false)
        val textInputLayoutPassword = view.findViewById<TextInputLayout>(R.id.editTextPassword)
        val editTextPassword = textInputLayoutPassword.editText
        val checkBoxEnablePassword = view.findViewById<CheckBox>(R.id.checkBoxEnablePassword)

        checkBoxEnablePassword.setOnCheckedChangeListener { _, isChecked ->
            textInputLayoutPassword.isErrorEnabled = false
            textInputLayoutPassword?.error = null
            textInputLayoutPassword?.isEnabled = isChecked
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(null)
            .setTitle("Backup Password")
            .setView(view)
            .setPositiveButton("Backup", null)
            .setNeutralButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (editTextPassword != null) {
                    val error = if (checkBoxEnablePassword.isChecked) {
                        validateBackupPasswordInput(editTextPassword.text.toString())
                    } else {
                        null
                    }

                    if (checkBoxEnablePassword.isChecked) {
                        textInputLayoutPassword.isErrorEnabled = true
                        textInputLayoutPassword.error = getStringError(error)
                    }

                    if (error != null) {
                        return@setOnClickListener
                    }

                    isPasswordEnabled = checkBoxEnablePassword.isChecked
                    password = if (isPasswordEnabled) editTextPassword.text.toString() else null
                    dialog.dismiss()
                    pickDir()
                }
            }
        }

        dialog.show()
    }

    private fun pickDir() {
        val fileName = "LemonTree_" + SimpleDateFormat("yyMMddHHmmss",
            Locale.getDefault()).format(Calendar.getInstance().timeInMillis)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/zip")
            .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/zip"))
            .putExtra(Intent.EXTRA_TITLE, fileName)

        startActivityForResult(intent, BACKUP_REQUEST_CODE)
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
                "password cannot be blank"
            }
            else -> {
                null
            }
        }
    }

    companion object {
        private const val EMPTY_PASSWORD = 0
        private const val BLANK_PASSWORD = 1

        private const val BACKUP_REQUEST_CODE = 7171

        fun newInstance() = BackupFragment()
    }

}