package com.reachfree.dailyexpense.ui.budget.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.ExpenseBudgetEntity
import com.reachfree.dailyexpense.databinding.CreateBudgetFragmentBinding
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.ui.budget.ExpenseBudgetViewModel
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.YearMonth

@AndroidEntryPoint
class CreateBudgetFragment : BaseDialogFragment<CreateBudgetFragmentBinding>() {

    private val viewModel: CreateBudgetViewModel by viewModels()
    private val createBudgetAdapter: CreateBudgetAdapter by lazy {
        CreateBudgetAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        val startOfMonth = AppUtils.startOfMonth(YearMonth.now())
        val endOfMonth = AppUtils.endOfMonth(YearMonth.now())
        viewModel.date.value = listOf(startOfMonth, endOfMonth)
    }

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CreateBudgetFragmentBinding {
        return CreateBudgetFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = "Create Budget"
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        binding.recyclerCategory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        createBudgetAdapter.setOnItemClickListener { categoryId ->
            showDialog(categoryId)
        }
    }

    private fun subscribeToObserver() {
        viewModel.expenseBudgetList.observe(this) { result ->
            val categories = Constants.expenseCategories()
            val createBudgetList = ArrayList<CreateBudgetModel>()

            for (i in categories.indices) {
                val createBudget = CreateBudgetModel()
                createBudget.category = categories[i]

                for (j in result.indices) {
                    if (categories[i].id == result[j].categoryId) {
                        createBudget.budgetedAmount = result[j].budgetAmount
                    }
                }

                createBudgetList.add(createBudget)
            }

            binding.recyclerCategory.adapter = createBudgetAdapter
            createBudgetAdapter.submitList(createBudgetList)
        }
    }

    private fun showDialog(categoryId: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_budget_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Set Budget")

        val alertDialog = builder.show()

        val edtAmount = dialogView.findViewById<EditText>(R.id.edtAmount)
        val btnOk = dialogView.findViewById<MaterialButton>(R.id.btnOk)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)

        edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(number: Editable?) {
                edtAmount.removeTextChangedListener(this)

                try {
                    var givenNumber = number.toString()
                    var longValue = 0L
                    if (givenNumber.contains(",")) {
                        givenNumber = givenNumber.replace(",".toRegex(), "")
                    }
                    longValue = givenNumber.toLong()
                    val formatter = DecimalFormat("#,###,###")
                    val formattedString = formatter.format(longValue)
                    edtAmount.setText(formattedString)
                    edtAmount.setSelection(edtAmount.text.length)

                } catch (e: NumberFormatException) {

                } catch (e: Exception) {

                }

                edtAmount.addTextChangedListener(this)
            }
        })

        btnOk.setOnClickListener {
            var amountValue = 0L

            try {
                var givenNumber = edtAmount.text.toString()
                if (givenNumber.contains(",")) {
                    givenNumber = givenNumber.replace(",".toRegex(), "")
                }
                amountValue = givenNumber.toLong()
            } catch (e: NumberFormatException) {

            } catch (e: Exception) {

            }

            updateBudget(categoryId, amountValue)
            alertDialog.dismiss()
        }

        btnCancel.setOnClickListener { alertDialog.dismiss() }
    }


    private fun updateBudget(categoryId: String, amountValue: Long) {
        viewModel.isExistExpenseBudget(categoryId)

        viewModel.isExistExpenseBudget.observe(this) {
            if (it == 0) {
                val expenseBudget = ExpenseBudgetEntity().apply {
                    this.categoryId = categoryId
                    this.amount = BigDecimal(amountValue)
                }
                viewModel.insertExpenseBudget(expenseBudget)
            } else {
                viewModel.getExpenseBudget(categoryId)
            }
        }

        viewModel.selectedExpenseBudget.observe(this) { result ->
            when (result.status) {
                Constants.Status.LOADING -> {

                }
                Constants.Status.SUCCESS -> {
                    result.data?.let {
                        val expenseBudget = it.apply {
                            this.amount = BigDecimal(amountValue)
                        }
                        viewModel.updateExpenseBudget(expenseBudget)
                    }
                }
                Constants.Status.ERROR -> {

                }
            }
        }
    }

    companion object {
        const val TAG = "CreateBudgetFragment"
        fun newInstance() = CreateBudgetFragment().apply {
            arguments = bundleOf()
        }
    }

}