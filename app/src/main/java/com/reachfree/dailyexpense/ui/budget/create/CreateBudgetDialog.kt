package com.reachfree.dailyexpense.ui.budget.create

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.databinding.DialogCreateBudgetBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.budget.detail.ExpenseBudgetDetailFragment
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.Status
import com.reachfree.dailyexpense.util.CurrencyUtils
import com.reachfree.dailyexpense.util.extension.changeBackgroundTintColor
import com.reachfree.dailyexpense.util.extension.load
import com.reachfree.dailyexpense.util.extension.runDelayed
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-13
 * Time: 오후 2:32
 */
@AndroidEntryPoint
class CreateBudgetDialog : BaseDialogFragment<DialogCreateBudgetBinding>() {

    private val viewModel: CreateBudgetViewModel by viewModels()

    private var categoryId: String? = null

    var decimals = true
    var current = ""
    var separator = ","

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryId = requireArguments().getString(CATEGORY_ID)
        categoryId?.let { viewModel.getExpenseBudget(it) }
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
    ): DialogCreateBudgetBinding {
        return DialogCreateBudgetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryId?.let {
            val category = AppUtils.getExpenseCategory(it)
            binding.txtCategoryName.text = requireContext().resources.getString(category.visibleNameResId)
            binding.imgCategoryIcon.load(category.iconResId)
            binding.imgCategoryIcon.changeBackgroundTintColor(category.backgroundColor)
        }

        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(number: Editable?) {
                if (number.toString() != current) {
                    binding.edtAmount.removeTextChangedListener(this)

                    val cleanString: String =
                        number.toString().replace("[$,.]".toRegex(), "").replace("".toRegex(), "")
                            .replace("\\s+".toRegex(), "")

                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed: Double
                            val parsedInt: Int
                            val formatted: String

                            when (Constants.currentCurrency.decimalPoint) {
                                2 -> {
                                    parsed = cleanString.toDouble()
                                    formatted = DecimalFormat("#,##0.00").format((parsed/100))
                                }
                                else -> {
                                    parsedInt = cleanString.toInt()
                                    formatted = DecimalFormat("#,###").format(parsedInt)
                                }
                            }

                            current = formatted

                            if (separator != "," && !decimals) {
                                binding.edtAmount.setText(formatted.replace(",".toRegex(), separator))
                            } else {
                                binding.edtAmount.setText(formatted)
                            }

                            binding.edtAmount.setSelection(formatted.length)
                        } catch (e: java.lang.NumberFormatException) {

                        }
                    }

                    binding.edtAmount.addTextChangedListener(this)
                }
            }
        })

        binding.btnSave.setOnSingleClickListener {
            saveBudget()
        }
        binding.btnCancel.setOnSingleClickListener { dismiss() }

        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel.selectedExpenseBudget.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it.data?.let { budget ->
                        binding.edtAmount.setText(budget.amount?.let { amount ->
                            when (Constants.currentCurrency.decimalPoint) {
                                2 -> {
                                    CurrencyUtils.edtAmountTextWithSecondDigit(amount)
                                }
                                else -> {
                                    CurrencyUtils.edtAmountTextWithZeroDigit(amount)
                                }
                            }
                        })
                    }
                }
                Status.ERROR -> {
                    it.error?.let {
                        Timber.d("error message $it")
                    }
                }
            }
        }
    }

    private fun saveBudget() {
        var amountValue = 0L

        try {
            var givenNumber = binding.edtAmount.text.toString()
            if (givenNumber.contains(",")) {
                givenNumber = givenNumber.replace(",".toRegex(), "")
            }
            amountValue = givenNumber.toLong()
        } catch (e: NumberFormatException) {

        } catch (e: Exception) {

        }

        categoryId?.let { categoryId ->
            viewModel.isExistExpenseBudget(categoryId)

            viewModel.isExistExpenseBudget.observe(this) {
                if (it == 0) {
                    val expenseBudget = ExpenseBudgetEntity().apply {
                        this.categoryId = categoryId
                        this.amount = BigDecimal(amountValue)
                    }
                    viewModel.insertExpenseBudget(expenseBudget)
                } else {
                    viewModel.updateExpenseBudget(BigDecimal(amountValue), categoryId)
                }
            }
        }

        runDelayed(300L) { dismiss() }
    }

    companion object {
        const val CATEGORY_ID = "categoryId"
        fun newInstance(categoryId: String) = CreateBudgetDialog().apply {
            arguments = bundleOf(CATEGORY_ID to categoryId)
        }
    }
}