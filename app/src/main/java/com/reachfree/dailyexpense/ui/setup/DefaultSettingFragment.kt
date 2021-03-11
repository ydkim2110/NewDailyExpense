package com.reachfree.dailyexpense.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.databinding.DefaultSettingFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.setup.currency.CurrencyFragment
import com.reachfree.dailyexpense.util.SessionManager
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DefaultSettingFragment : BaseFragment<DefaultSettingFragmentBinding>() {

    @Inject lateinit var sessionManager: SessionManager

    private lateinit var onDefaultSettingDoneClicked: OnDefaultSettingDoneClicked
    private var currency: Currency? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is SetupActivity) {
            onDefaultSettingDoneClicked = activity as SetupActivity
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DefaultSettingFragmentBinding {
        return DefaultSettingFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCurrency.setOnSingleClickListener {
            val currencyFragment = CurrencyFragment.newInstance()
            currencyFragment.show(childFragmentManager, null)
        }

        childFragmentManager.setFragmentResultListener("currency", this) { key, bundle ->
            currency = bundle.getParcelable("data")
            binding.btnCurrency.text = currency?.flag
        }

        binding.btnDone.setOnSingleClickListener {
            if (isValidate()) {
                saveDefaultSetting()
            }
        }
    }

    private fun saveDefaultSetting() {
        if (this::onDefaultSettingDoneClicked.isInitialized) {
            currency?.let {
                onDefaultSettingDoneClicked.onDefaultSettingDone(
                    binding.textFieldNickname.editText?.text.toString().trim(),
                    it
                )
            }
        }
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

    interface OnDefaultSettingDoneClicked {
        fun onDefaultSettingDone(nickname: String, currency: Currency)
    }

    companion object {
        fun newInstance() = DefaultSettingFragment()
    }
}