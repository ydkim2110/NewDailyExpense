package com.reachfree.dailyexpense.ui.settings.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.databinding.ProfileFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseDialogFragment<ProfileFragmentBinding>() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ProfileFragmentBinding {
        return ProfileFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textFieldNickname.editText?.setText(sessionManager.getUser().nickname)

        setupToolbar()
        setupViewHandler()
    }

    private fun setupToolbar() {
        binding.appBar.btnBack.setOnSingleClickListener {
            dismiss()
        }

        binding.appBar.txtToolbarTitle.text = getString(R.string.toolbar_title_profile)
    }

    private fun setupViewHandler() {
        binding.btnDone.setOnSingleClickListener {
            if (isValidate()) {
                val nickname = binding.textFieldNickname.editText?.text.toString().trim()
                sendResult(nickname)
                dismiss()
            }
        }
    }

    private fun sendResult(nickname: String) {
        val data = Bundle().apply {
            putParcelable("data", User(nickname))
        }
        setFragmentResult("profile", data)
    }

    private fun isValidate(): Boolean {
        val isNicknameEmpty = binding.textFieldNickname.editText?.text.toString().trim().isEmpty()
        if (isNicknameEmpty) {
            binding.textFieldNickname.error = getString(R.string.this_field_is_required)
        } else {
            binding.textFieldNickname.error = null
        }
        return !(isNicknameEmpty)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

}