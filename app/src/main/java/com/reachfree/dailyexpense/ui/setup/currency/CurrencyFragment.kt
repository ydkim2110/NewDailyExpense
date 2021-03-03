package com.reachfree.dailyexpense.ui.setup.currency

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.databinding.CurrencyFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.base.BaseFragment
import com.reachfree.dailyexpense.ui.dashboard.pattern.PatternDetailFragment
import com.reachfree.dailyexpense.ui.setup.DefaultSettingFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.extension.afterTextChanged
import com.reachfree.dailyexpense.util.extension.runDelayed
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import timber.log.Timber
import java.util.*

class CurrencyFragment : BaseDialogFragment<CurrencyFragmentBinding>() {

    private val currencyAdapter by lazy {
        CurrencyAdapter()
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CurrencyFragmentBinding {
        return CurrencyFragmentBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupViewHandler()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnSingleClickListener {
            dismiss()
        }

        binding.txtToolbarTitle.text = "Currency Selection"
    }

    private fun setupRecyclerView() {
        binding.recyclerCurrency.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currencyAdapter
            currencyAdapter.submitList(Currency.values().toList())
        }
    }

    private fun setupViewHandler() {
        currencyAdapter.setOnItemClickListener { currency ->
            sendResult(currency)
            dismiss()
        }

        binding.edtSearch.afterTextChanged {
            searchCurrency()
        }
    }

    private fun sendResult(currency: Currency) {
        val data = Bundle().apply {
            putParcelable("data", currency)
        }
        setFragmentResult("currency", data)
    }

    private fun searchCurrency() {
        runDelayed(SEARCH_DELAY) {
            val query = binding.edtSearch.text.toString().trim()
            currencyAdapter.submitList(filteredCurrencies(query))
        }
    }

    private fun filteredCurrencies(query: String): List<Currency> {
        return Currency.values().filter { currency ->
            val code = currency.code.toLowerCase(Locale.getDefault())
            val title = currency.title.toLowerCase(Locale.getDefault())
            code.contains(query.toLowerCase(Locale.getDefault()))
                    || title.contains(query.toLowerCase(Locale.getDefault()))
        }
    }

    companion object {
        private const val SEARCH_DELAY = 500L

        fun newInstance() = CurrencyFragment().apply {
            arguments = bundleOf()
        }
    }
}