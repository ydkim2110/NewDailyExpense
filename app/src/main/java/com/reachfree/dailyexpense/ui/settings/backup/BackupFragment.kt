package com.reachfree.dailyexpense.ui.settings.backup

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.LocalDatabase
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.databinding.BackupFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.viewmodel.TransactionViewModel
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.ProgressBarDialog
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.apache.commons.io.FilenameUtils
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.system.exitProcess

@AndroidEntryPoint
class BackupFragment : BaseDialogFragment<BackupFragmentBinding>(),
    BackupPasswordDialog.BackupPasswordResult, RestorePasswordDialog.RestorePasswordResult,
    ConfirmationDialog.ConfirmationDialogResult,
    DialogInterface.OnDismissListener {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var database: LocalDatabase

    private val viewModel: TransactionViewModel by viewModels()

    private var password: String? = null
    private var isPasswordEnabled = false

    private var toBeRestoredZipFileGlobal: File? = null
    private var extractedFilesDirGlobal: File? = null
    private var preparedZipFileGlobal: ZipFile? = null

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
        binding.appBar.txtToolbarTitle.text = getString(R.string.toolbar_title_backup_and_restore)
    }

    private fun setupViewHandler() {
        binding.btnCreateBackup.setOnSingleClickListener {
            val backupPasswordDialog = BackupPasswordDialog()
            backupPasswordDialog.show(childFragmentManager, null)

            backupPasswordDialog.setBackupPasswordResult(this)
        }
        binding.btnRestore.setOnSingleClickListener {
            showRestore()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == BACKUP_REQUEST_CODE) {
                showProgressBar()
                backupLocal(data)
            } else if (requestCode == RESTORE_REQUEST_CODE) {
                showProgressBar()
                restoreLocal(data)
            }
        } else {
            if (requestCode == BACKUP_REQUEST_CODE) {
                isPasswordEnabled = false
                password = null
            }
        }
    }

    /**
     * Back up
     */
    private fun backupLocal(data: Intent) {
        lifecycleScope.launch(Dispatchers.IO) {
            val uri = data.data!!
            val pfd = requireContext().contentResolver.openFileDescriptor(uri, "w")
            pfd?.use {
                FileOutputStream(pfd.fileDescriptor).use {
                    withContext(Dispatchers.Main) {
                        viewModel.checkpoint()
                    }
                    val zipFile = getBackupZipFile()
                    if (zipFile != null) {
                        try {
                            zipFile.inputStream().use { input -> input.copyTo(it) }
                            withContext(Dispatchers.Main) {
                                showBackupSuccessfulMessage()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                showBackupFailedMessage()
                            }
                        } finally {
                            if (zipFile.exists()) {
                                zipFile.delete()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showRestoreFailedMessage()
                        }
                    }
                }
            }

            isPasswordEnabled = false
            password = null
        }
    }

    override fun onBackupPasswordFinished(isPasswordEnabled: Boolean, password: String?) {
        this.isPasswordEnabled = isPasswordEnabled
        this.password = password
        pickDir()
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


    /**
     * Restore up
     */
    private fun showRestore() {
        val mimeTypes = arrayOf(
            "application/zip",
            "application/octet-stream",
            "application/x-zip-compressed",
            "multipart/x-zip"
        )
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.CATEGORY_OPENABLE, mimeTypes)
            .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(intent, RESTORE_REQUEST_CODE)
    }

    private fun restoreLocal(data: Intent) {
        lifecycleScope.launch(Dispatchers.IO) {
            val uri = data.data!!
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val pfd = requireContext().contentResolver.openFileDescriptor(uri, "r")
            pfd?.use { parcelFileDescriptor ->
                FileInputStream(parcelFileDescriptor.fileDescriptor).use { inputStream ->
                    var toBeRestoredZipFile: File? = null
                    var extractedFilesDir: File? = null
                    var delete = true

                    try {
                        val dataDir = requireNotNull(requireContext().filesDir.parentFile)

                        toBeRestoredZipFile = File(dataDir.absolutePath + "/toBeRestored.zip")
                        extractedFilesDir = File(dataDir.absolutePath + "/toBeRestoredDir")

                        deleteTempRestoreFiles(toBeRestoredZipFile, extractedFilesDir)

                        extractedFilesDir.mkdir()

                        inputStream.use { input ->
                            toBeRestoredZipFile.outputStream().use { output -> input.copyTo(output) }
                        }

                        val preparedZipFile = ZipFile(toBeRestoredZipFile.absolutePath)

                        toBeRestoredZipFileGlobal = toBeRestoredZipFile
                        extractedFilesDirGlobal = extractedFilesDir
                        preparedZipFileGlobal = preparedZipFile

                        if (preparedZipFile.isEncrypted) {
                            delete = false
                            withContext(Dispatchers.Main) {
                                val restorePasswordDialog = RestorePasswordDialog()
                                restorePasswordDialog.show(childFragmentManager, null)

                                restorePasswordDialog.setRestorePasswordResult(this@BackupFragment)
                            }
                        } else {
                            delete = false
                            withContext(Dispatchers.Main) {
                                showConfirmRestoreDialog(false)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            showRestoreFailedMessage()
                        }
                    } finally {
                        if (delete) {
                            deleteTempRestoreFiles(toBeRestoredZipFile, extractedFilesDir)
                        }
                    }
                }
            }
        }
    }

    override fun onSuccess(pw: String) {
        restoreWithPassword(pw, toBeRestoredZipFileGlobal!!, extractedFilesDirGlobal!!)
    }

    override fun onCancel() {
        deleteTempRestoreFiles(toBeRestoredZipFileGlobal!!, extractedFilesDirGlobal!!)
        showRestoreCancelled()
    }

    private fun showConfirmRestoreDialog(withPassword: Boolean) {
        val confirmationDialog = ConfirmationDialog.newInstance(withPassword)
        confirmationDialog.show(childFragmentManager, null)

        confirmationDialog.setBackupPasswordResult(this)

        childFragmentManager.executePendingTransactions()
        confirmationDialog.dialog?.setOnDismissListener {
            dismiss()
            hideProgressBar()
        }
    }

    override fun onConfirmationDialogFinished(result: Int, withPassword: Boolean) {
        if (result == CONFIRM) {
            if (withPassword) {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        restoreData(extractedFilesDirGlobal!!)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            showRestoreFailedMessage()
                        }
                    } finally {
                        deleteTempRestoreFiles(toBeRestoredZipFileGlobal!!, extractedFilesDirGlobal!!)
                    }
                    withContext(Dispatchers.Main) {
                        showRestoreSuccessful()
                    }
                }
            } else {
                restoreWithoutPassword(preparedZipFileGlobal!!, toBeRestoredZipFileGlobal!!, extractedFilesDirGlobal!!)
            }
        } else if (result == CANCEL) {
            deleteTempRestoreFiles(toBeRestoredZipFileGlobal!!, extractedFilesDirGlobal!!)
            showRestoreCancelled()
        }
    }

    private fun restoreWithoutPassword(
        preparedZipFile: ZipFile,
        toBeRestoredZipFile: File,
        extractedFilesDir: File
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                preparedZipFile.extractAll(
                    extractedFilesDir.absolutePath
                )
                restoreData(extractedFilesDir)
                withContext(Dispatchers.Main) {
                    showRestoreSuccessful()
                }
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    showRestoreFailedMessage()
                }
            } finally {
                deleteTempRestoreFiles(toBeRestoredZipFile, extractedFilesDir)
            }
        }
    }

    private fun getRandom4DigitCode(): Int {
        return ((Math.random() * 9000) + 1000).toInt()
    }

    private fun showRestoreCancelled() {
        showDialog(
            null,
            getString(R.string.text_restore_cancelled)
        )
        hideProgressBar()
    }

    private fun restoreWithPassword(pw: String, toBeRestoredZipFile: File, extractedFilesDir: File) {
        lifecycleScope.launch(Dispatchers.IO) {
            var delete = true
            try {
                val zf = ZipFile(toBeRestoredZipFile.absolutePath, pw.toCharArray())
                zf.extractAll(extractedFilesDir.absolutePath)
                delete = false
                withContext(Dispatchers.Main) {
                    showConfirmRestoreDialog(true)
                }
            } catch (e: net.lingala.zip4j.exception.ZipException) {
                withContext(Dispatchers.Main) {
                    showWrongPasswordMessage()
                }
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    showRestoreFailedMessage()
                }
            } finally {
                if (delete) {
                    deleteTempRestoreFiles(toBeRestoredZipFile, extractedFilesDir)
                }
            }
        }
    }

    private fun restoreData(extractedFilesDir: File) {
        extractedFilesDir.listFiles()?.forEach { file ->
            when (FilenameUtils.removeExtension(file.name)) {
                Constants.LOCAL_DATABASE_NAME -> {
                    restoreDatabase(file)
                }
                Constants.PROFILE_JSON_FILE_NAME -> {
                    restoreProfile(file)
                }
            }
        }
    }

    private fun restoreProfile(file: File) {
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        val stringBuilder = StringBuilder()
        var line = bufferedReader.readLine()
        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = bufferedReader.readLine()
        }
        bufferedReader.close()

        val json = JSONObject(stringBuilder.toString())
        try {
            sessionManager.saveUser(User(json.getString(Constants.PREF_KEY_NICKNAME)))
        } catch (e: Exception) {

        }
    }

    private fun restoreDatabase(file: File) {
        val dbFile = requireContext().getDatabasePath(Constants.LOCAL_DATABASE_NAME)
        database.close()
        val fileInputStream = FileInputStream(file)
        fileInputStream.use { input ->
            val fileOutputStream = FileOutputStream(dbFile)
            fileOutputStream.use { output ->
                val buf = ByteArray(1024)
                var len: Int
                while (input.read(buf).also { len = it} > 0) {
                    output.write(buf, 0, len)
                }
            }
        }
    }

    private fun showRestoreSuccessful() {
        try {
            AppUtils.hideKeyboard(requireActivity())
        } catch (e: Exception) {
        }

        val dialog = MaterialAlertDialogBuilder(requireContext()).setMessage(getString(R.string.text_restore_successful_restart_app))
            .setTitle(getString(R.string.text_restore_successful))
            .setPositiveButton(
                getString(R.string.text_restart)
            ) { _, _ -> }
            .setOnDismissListener {
                restart()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        dialog.show()

        hideProgressBar()
    }

    private fun restart() {
        val pm = requireActivity().packageManager
        val intent = pm.getLaunchIntentForPackage(requireActivity().packageName)
        requireActivity().finishAffinity()
        requireActivity().startActivity(intent)
        exitProcess(0)
    }

    private fun showWrongPasswordMessage() {
        showDialog(
            getString(R.string.text_wrong_password),
            getString(R.string.text_restore_failed)
        )
        hideProgressBar()
    }

    private fun deleteTempRestoreFiles(toBeRestoreZipFile: File?, extractedFilesDir: File?) {
        if (extractedFilesDir?.exists() == true) {
            extractedFilesDir.deleteRecursively()
        }
        if (toBeRestoreZipFile?.exists() == true) {
            toBeRestoreZipFile.delete()
        }
    }

    private fun showBackupSuccessfulMessage() {
        showDialog(
            getString(R.string.text_backup_created_successfully),
            getString(R.string.text_backup_successful)
        )
        hideProgressBar()
    }

    private fun showDialog(message: String?, title: String, onDismiss: () -> Unit = {}) {
        try {
            val dialog = MaterialAlertDialogBuilder(requireContext()).setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.text_okay) { _, _ -> }
                .setOnDismissListener { onDismiss() }
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }

            dialog.show()
        } catch (e: Exception) {

        }

    }

    private fun showBackupFailedMessage() {
        showDialog(
            getString(R.string.text_error_while_performing_backup),
            getString(R.string.text_backup_failed)
        )
        hideProgressBar()
    }

    private fun showRestoreFailedMessage() {
        showDialog(
            getString(R.string.text_error_while_performing_restore),
            getString(R.string.text_restore_failed)
        )
        hideProgressBar()
    }

    private fun getBackupZipFile(): File? {
        val dbFile: File = requireContext().getDatabasePath(Constants.LOCAL_DATABASE_NAME)
        val profileJsonFile =
            File(requireContext().filesDir.path + "/${Constants.PROFILE_JSON_FILE_NAME}.json")
        val zipFile = File(requireContext().filesDir.path + "/backup.zip")

        val profileJson = getProfileJson()

        try {
            val output: Writer?
            output = BufferedWriter(FileWriter(profileJsonFile))
            output.write(profileJson)
            output.close()
        } catch (e: Exception) {
            return  null
        }

        if (isPasswordEnabled && password != null) {
            val encZipFile = ZipFile(zipFile.absolutePath, password!!.toCharArray())
            val zipParameters = ZipParameters()
            zipParameters.isEncryptFiles = true
            zipParameters.encryptionMethod = EncryptionMethod.AES

            encZipFile.addFile(profileJsonFile, zipParameters)
            encZipFile.addFile(dbFile, zipParameters)
        } else {
            val encZipFile = ZipFile(zipFile.absolutePath)

            encZipFile.addFile(profileJsonFile)
            encZipFile.addFile(dbFile)

            if (profileJsonFile.exists()) {
                profileJsonFile.delete()
            }
        }

        return zipFile
    }

    private fun getProfileJson(): String {
        val profile: MutableMap<String, String> = HashMap()
        profile[Constants.PREF_KEY_NICKNAME] = sessionManager.getUser().nickname
        return Gson().toJson(profile)
    }

    private fun showProgressBar() {
        progressBarDialog.show()
    }

    private fun hideProgressBar() {
        progressBarDialog.dismiss()
    }

    companion object {
        private const val CONFIRM = 0
        private const val CANCEL = 1

        private const val BACKUP_REQUEST_CODE = 7171
        private const val RESTORE_REQUEST_CODE = 8282

        fun newInstance() = BackupFragment()
    }


}