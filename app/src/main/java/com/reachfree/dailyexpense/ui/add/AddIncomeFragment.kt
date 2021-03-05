package com.reachfree.dailyexpense.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.AddIncomeFragmentBinding
import com.reachfree.dailyexpense.ui.add.bottomsheet.AddCategoryBottomSheet
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.PATTERN
import com.reachfree.dailyexpense.util.Constants.PAYMENT
import com.reachfree.dailyexpense.util.Constants.TYPE
import com.reachfree.dailyexpense.util.Constants.TYPE_INCOME
import com.reachfree.dailyexpense.util.extension.runDelayed
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class AddIncomeFragment : DialogFragment() {

    private var _binding: AddIncomeFragmentBinding? = null
    private val binding  get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()
    private val addCategoryBottomSheet: AddCategoryBottomSheet by lazy {
        AddCategoryBottomSheet(TYPE_INCOME)
    }

    private var category = Constants.incomeCategories().first()

    private var selectedDate = Calendar.getInstance()
    private val dateToday = Calendar.getInstance()
    private val dateYesterday = Calendar.getInstance()
    private var chipGroupSelected: Int = Constants.TODAY

    private var selectedCategory = Constants.SALARY

    private var passedTransaction: TransactionEntity? = null
    private var passedDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        dateYesterday.add(Calendar.DAY_OF_MONTH, -1)

        passedTransaction = requireArguments().getParcelable(TRANSACTION)
        passedTransaction?.let {
            viewModel.getTransactionById(it.id)
        }

        passedDate = requireArguments().getLong(DATE, Date().time)
        passedDate?.let {
            selectedDate.time = Date(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddIncomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = requireContext().resources.getString(R.string.toolbar_title_add_income)
        binding.appBar.btnAction.visibility = View.VISIBLE
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
        binding.appBar.btnAction.setOnSingleClickListener { saveIncome() }
    }

    private fun setupView() {
        if (passedTransaction == null) {
            binding.chipGroupDate.check(R.id.chipToday)
            dateSet(selectedDate[Calendar.YEAR], selectedDate[Calendar.MONTH], selectedDate[Calendar.DAY_OF_MONTH])
            binding.btnCategory.text = resources.getString(category.visibleNameResId)
        }

        binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(passedDate)
    }

    private fun setupViewHandler() {


        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(number: Editable?) {
                binding.edtAmount.removeTextChangedListener(this)

                try {
                    var givenNumber = number.toString()
                    val longValue: Long
                    if (givenNumber.contains(",")) {
                        givenNumber = givenNumber.replace(",".toRegex(), "")
                    }
                    longValue = givenNumber.toLong()
                    val formatter = DecimalFormat("#,###,###")
                    val formattedString = formatter.format(longValue)
                    binding.edtAmount.setText(formattedString)
                    binding.edtAmount.setSelection(binding.edtAmount.text.length)

                } catch (e: NumberFormatException) {

                } catch (e: Exception) {

                }

                binding.edtAmount.addTextChangedListener(this)
            }
        })

        binding.btnCategory.setOnSingleClickListener {
            addCategoryBottomSheet.isCancelable = true
            addCategoryBottomSheet.show(childFragmentManager, AddCategoryBottomSheet.TAG)
        }

        addCategoryBottomSheet.setOnCategoryItemSelected(object : AddCategoryBottomSheet.OnCategoryItemSelected {
            override fun onSelectedCategory(category: Category) {
                selectedCategory = category.id
                binding.btnCategory.text = resources.getString(category.visibleNameResId)
            }
        })

        binding.chipGroupDate.setOnCheckedChangeListener { _, checkedId ->
            binding.chipToday.isSelected = false
            binding.chipYesterday.isSelected = false
            binding.chipPickDate.isSelected = false

            when (checkedId) {
                R.id.chipToday -> {
                    binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(dateToday.time)
                    selectedDate = dateToday
                    chipGroupSelected = Constants.TODAY
                }
                R.id.chipYesterday -> {
                    binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(dateYesterday.time)
                    selectedDate = dateYesterday
                    chipGroupSelected = Constants.YESTERDAY
                }
            }
        }

        binding.chipPickDate.setOnClickListener {
            DatePickerFragment.newInstance().apply {
                dateSelected = {year, month, dayOfMonth -> dateSet(year, month, dayOfMonth) }
            }.show(childFragmentManager, DatePickerFragment.TAG)
        }

        binding.btnDelete.setOnSingleClickListener {
            passedTransaction?.let {
                viewModel.deleteTransaction(it)
            }
            runDelayed(CLOSE_DELAY) { dismiss() }
        }
    }

    private fun saveIncome() {
        if (!AppUtils.validated(binding.edtAmount, binding.edtDescription)) return

        val descriptionValue = binding.edtDescription.text.toString().trim()
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

        if (amountValue <= 0L) {
            binding.edtAmount.error = getString(R.string.text_must_be_greater_than_zero)
            return
        }

        if (passedTransaction == null) {
            val transaction = TransactionEntity().apply {
                description = descriptionValue
                amount = BigDecimal(amountValue)
                type = TYPE.INCOME.ordinal
                payment = PAYMENT.INCOME.ordinal
                pattern = PATTERN.INCOME.ordinal
                categoryId = selectedCategory
                subCategoryId = selectedCategory
                registerDate = selectedDate.time.time
            }

            viewModel.insertTransaction(transaction)
        } else {
            val transaction = passedTransaction?.apply {
                description = descriptionValue
                amount = BigDecimal(amountValue)
                categoryId = selectedCategory
                subCategoryId = selectedCategory
                registerDate = selectedDate.time.time
            }
            transaction?.let {
                viewModel.updateTransaction(transaction)
            }
        }

        runDelayed(CLOSE_DELAY) { dismiss() }
    }

    private fun subscribeToObserver() {
        viewModel.transaction.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Constants.Status.LOADING -> {

                }
                Constants.Status.SUCCESS -> {
                    result.data?.let {
                        val passedCategory = AppUtils.getIncomeCategory(it.categoryId)

                        binding.btnDelete.visibility = View.VISIBLE
                        binding.edtAmount.setText(it.amount?.let { amount -> AppUtils.insertComma(amount) })
                        binding.edtDescription.setText(it.description)
                        binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(it.registerDate)
                        binding.btnCategory.text = requireContext().resources.getString(passedCategory.visibleNameResId)

                        category = passedCategory
                        selectedCategory = category.id
                        selectedDate = AppUtils.convertDateToCalendar(Date(it.registerDate))

                        val todayStart = AppUtils.calculateStartOfDay(LocalDate.now()).toMillis()!!
                        val yesterdayStart = AppUtils.calculateStartOfDay(LocalDate.now().minusDays(1)).toMillis()!!

                        binding.chipToday.isSelected = false
                        binding.chipYesterday.isSelected = false
                        binding.chipPickDate.isSelected = false

                        when {
                            it.registerDate > todayStart -> {
                                binding.chipToday.isSelected = true
                            }
                            it.registerDate > yesterdayStart -> {
                                binding.chipYesterday.isSelected = true
                            }
                            else -> {
                                binding.chipPickDate.isSelected = true
                            }
                        }

                    }
                }
                Constants.Status.ERROR -> {

                }
            }
        }
    }

    private fun dateSet(year: Int, month: Int, dayOfMonth: Int) {
        selectedDate.set(Calendar.YEAR, year)
        selectedDate.set(Calendar.MONTH, month)
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(selectedDate.time)
        chipGroupSelected = Constants.PICK_DATE
    }

    companion object {
        private const val CLOSE_DELAY = 300L
        private const val TRANSACTION = "transaction"
        private const val DATE = "date"
        fun newInstance(transaction: TransactionEntity? = null, dateLong: Long? = null) = AddIncomeFragment().apply {
            arguments = Bundle().apply {
                putParcelable(TRANSACTION, transaction)
                dateLong?.let { putLong(DATE, it) }
            }
        }
    }
}