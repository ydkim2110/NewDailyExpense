package com.reachfree.dailyexpense.ui.add

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.SubCategory
import com.reachfree.dailyexpense.data.model.TransactionEntity
import com.reachfree.dailyexpense.databinding.AddExpenseFragmentBinding
import com.reachfree.dailyexpense.ui.add.bottomsheet.AddCategoryBottomSheet
import com.reachfree.dailyexpense.ui.add.bottomsheet.AddSubCategoryBottomSheet
import com.reachfree.dailyexpense.ui.base.BaseDialogFragment
import com.reachfree.dailyexpense.util.*
import com.reachfree.dailyexpense.util.Constants.PATTERN.*
import com.reachfree.dailyexpense.util.Constants.PAYMENT
import com.reachfree.dailyexpense.util.Constants.Status
import com.reachfree.dailyexpense.util.Constants.TYPE.EXPENSE
import com.reachfree.dailyexpense.util.Constants.TYPE_EXPENSE
import com.reachfree.dailyexpense.util.extension.runDelayed
import com.reachfree.dailyexpense.util.extension.setColorFilter
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddExpenseFragment : BaseDialogFragment<AddExpenseFragmentBinding>() {

    @Inject lateinit var sessionManager: SessionManager

    private val viewModel: AddTransactionViewModel by viewModels()
    private val addCategoryBottomSheet: AddCategoryBottomSheet by lazy {
        AddCategoryBottomSheet(TYPE_EXPENSE)
    }

    private var category = Constants.expenseCategories().first()
    private lateinit var addSubCategoryBottomSheet: AddSubCategoryBottomSheet

    private var selectedDate = Calendar.getInstance()
    private val dateToday = Calendar.getInstance()
    private val dateYesterday = Calendar.getInstance()
    private var chipGroupSelected: Int = Constants.TODAY

    private var selectedPayment = PAYMENT.CREDIT.ordinal
    private var selectedPattern = NORMAL.ordinal
    private var selectedCategory = Constants.FOOD_DRINKS
    private var selectedSubCategory = Constants.EATING_OUT

    private var passedTransaction: TransactionEntity? = null
    private var passedDate: Long? = null

    private val dropDownIcon
        get() = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_drop_down, null)

    var decimals = true
    var current = ""
    var separator = ","

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

    override fun getDialogFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddExpenseFragmentBinding {
        return AddExpenseFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupViewHandler()
        subscribeToObserver()
    }

    private fun setupToolbar() {
        binding.appBar.txtToolbarTitle.text = getString(R.string.toolbar_title_add_expense)
        binding.appBar.btnAction.visibility = View.VISIBLE
        binding.appBar.btnBack.setOnSingleClickListener { dismiss() }
        binding.appBar.btnAction.setOnSingleClickListener { saveExpense() }
    }

    private fun setupView() {
        if (passedTransaction == null) {
            binding.chipGroupDate.check(R.id.chipToday)
            dateSet(
                selectedDate[Calendar.YEAR],
                selectedDate[Calendar.MONTH],
                selectedDate[Calendar.DAY_OF_MONTH]
            )

            binding.btnCategory.text = resources.getString(category.visibleNameResId)
            val subCategoryList = Constants.expenseSubCategories().filter { it.categoryId == category.id}
            val resId = subCategoryList.firstOrNull()?.visibleNameResId ?: category.visibleNameResId
            binding.btnSubCategory.text = resources.getString(resId)

            changeColors(NORMAL.ordinal)
        }

        binding.txtCurrency.text = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol
        binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(passedDate)
        binding.btnPaymentToggleGroup.check(binding.btnCredit.id)

        changeColorCategory(AppUtils.getExpenseCategory(Constants.FOOD_DRINKS))
        changeColorSubCategory(AppUtils.getExpenseSubCategory(Constants.EATING_OUT))
    }

    private fun setupViewHandler() {
        binding.btnPaymentToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) group.check(checkedId)

            if (isChecked) {
                when (binding.btnPaymentToggleGroup.checkedButtonId) {
                    binding.btnCredit.id -> selectedPayment = PAYMENT.CREDIT.ordinal
                    binding.btnCash.id -> selectedPayment = PAYMENT.CASH.ordinal
                }
            }

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

        binding.btnCategory.setOnSingleClickListener {
            addCategoryBottomSheet.isCancelable = true
            addCategoryBottomSheet.show(childFragmentManager, AddCategoryBottomSheet.TAG)
        }

        addCategoryBottomSheet.setOnCategoryItemSelected(object :
            AddCategoryBottomSheet.OnCategoryItemSelected {
            override fun onSelectedCategory(category: Category) {
                this@AddExpenseFragment.category = category
                selectedCategory = category.id
                binding.btnCategory.text = resources.getString(category.visibleNameResId)

                val subCategory =
                    Constants.expenseSubCategories().filter { it.categoryId == category.id }
                val nameResId =
                    subCategory.firstOrNull()?.visibleNameResId ?: category.visibleNameResId

                selectedSubCategory = subCategory.firstOrNull()?.id ?: category.id
                binding.btnSubCategory.text = resources.getString(nameResId)

                changeColorCategory(category)
                changeColorSubCategory(subCategory.first())
            }
        })

        binding.btnSubCategory.setOnSingleClickListener {
            val isExistSubCategory =
                Constants.expenseSubCategories().any { it.categoryId == category.id }

            if (!isExistSubCategory) {
                return@setOnSingleClickListener
            }

            addSubCategoryBottomSheet = AddSubCategoryBottomSheet(category)
            addSubCategoryBottomSheet.isCancelable = true
            addSubCategoryBottomSheet.show(childFragmentManager, AddSubCategoryBottomSheet.TAG)

            addSubCategoryBottomSheet.setOnSubCategoryItemSelected(object :
                AddSubCategoryBottomSheet.OnSubCategoryItemSelected {
                override fun onSelectedSubCategory(subCategory: SubCategory) {
                    selectedSubCategory = subCategory.id
                    binding.btnSubCategory.text = resources.getString(subCategory.visibleNameResId)
                    changeColorSubCategory(subCategory)
                }
            })
        }

        binding.chipGroupDate.setOnCheckedChangeListener { _, checkedId ->
            binding.chipToday.isSelected = false
            binding.chipYesterday.isSelected = false
            binding.chipPickDate.isSelected = false

            when (checkedId) {
                R.id.chipToday -> {
                    binding.txtDatePicked.text =
                        AppUtils.addTransactionDateFormat.format(dateToday.time)
                    selectedDate = dateToday
                    chipGroupSelected = Constants.TODAY
                }
                R.id.chipYesterday -> {
                    binding.txtDatePicked.text = AppUtils.addTransactionDateFormat.format(
                        dateYesterday.time
                    )
                    selectedDate = dateYesterday
                    chipGroupSelected = Constants.YESTERDAY
                }
            }
        }

        binding.chipPickDate.setOnSingleClickListener {
            DatePickerFragment.newInstance().apply {
                dateSelected = { year, month, dayOfMonth -> dateSet(year, month, dayOfMonth) }
            }.show(childFragmentManager, DatePickerFragment.TAG)
        }

        binding.btnDelete.setOnSingleClickListener {
            passedTransaction?.let {
                viewModel.deleteTransaction(it)
            }
            runDelayed(CLOSE_DELAY) { dismiss() }
        }

        binding.chipNormal.setOnClickListener { changeColors(NORMAL.ordinal) }
        binding.chipWaste.setOnClickListener { changeColors(WASTE.ordinal) }
        binding.chipInvest.setOnClickListener { changeColors(INVEST.ordinal) }

    }

    private fun changeColorCategory(category: Category) {
        val categoryIcon = ResourcesCompat.getDrawable(resources, category.iconResId, null)

        categoryIcon?.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                category.backgroundColor
            )
        )

        binding.btnCategory.setCompoundDrawablesWithIntrinsicBounds(
            categoryIcon,
            null,
            dropDownIcon,
            null
        )
    }

    private fun changeColorSubCategory(subCategory: SubCategory) {
        val subCategoryIcon = ResourcesCompat.getDrawable(
            resources,
            subCategory.iconResId,
            null
        )

        subCategoryIcon?.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                subCategory.backgroundColor
            )
        )

        binding.btnSubCategory.setCompoundDrawablesWithIntrinsicBounds(
            subCategoryIcon,
            null,
            dropDownIcon,
            null
        )
    }

    private fun subscribeToObserver() {
        viewModel.transaction.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    result.data?.let {
                        val passedCategory = AppUtils.getExpenseCategory(it.categoryId)
                        val passedSubCategory = AppUtils.getExpenseSubCategory(it.subCategoryId)
                        binding.btnDelete.visibility = View.VISIBLE
                        binding.edtAmount.setText(it.amount?.let { amount ->
                            when (Constants.currentCurrency.decimalPoint) {
                                2 -> {
                                    CurrencyUtils.edtAmountTextWithSecondDigit(amount)
                                }
                                else -> {
                                    CurrencyUtils.edtAmountTextWithZeroDigit(amount)
                                }
                            }
                        })
                        binding.edtDescription.setText(it.description)
                        binding.txtDatePicked.text =
                            AppUtils.addTransactionDateFormat.format(it.registerDate)

                        binding.btnCategory.text = requireContext().resources.getString(
                            passedCategory.visibleNameResId
                        )
                        binding.btnSubCategory.text = requireContext().resources.getString(
                            passedSubCategory.visibleNameResId
                        )

                        changeColorCategory(passedCategory)
                        changeColorSubCategory(passedSubCategory)


                        when (it.payment) {
                            0 -> binding.btnPaymentToggleGroup.check(binding.btnCredit.id)
                            1 -> binding.btnPaymentToggleGroup.check(binding.btnCash.id)
                        }

                        category = passedCategory
                        selectedPayment = it.payment
                        selectedCategory = category.id
                        selectedSubCategory = passedSubCategory.id
                        selectedDate = AppUtils.convertDateToCalendar(Date(it.registerDate))

                        val todayStart = AppUtils.calculateStartOfDay(LocalDate.now()).toMillis()!!
                        val yesterdayStart = AppUtils.calculateStartOfDay(
                            LocalDate.now().minusDays(
                                1
                            )
                        ).toMillis()!!

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

                        changeColors(it.pattern)
                    }
                }
                Status.ERROR -> {

                }
            }
        }
    }

    private fun saveExpense() {
        if (!AppUtils.validated(binding.edtAmount, binding.edtDescription)) return

        val descriptionValue = binding.edtDescription.text.toString().trim()
        var amountValue = BigDecimal(0)

        try {
            amountValue = binding.edtAmount.text.toString().trim().replace("[$,]".toRegex(), "")
                .replace(Constants.currentCurrency.symbol, "").toBigDecimal()
        } catch (e: NumberFormatException) {
            Timber.d("ERROR: $e")
        } catch (e: Exception) {
            Timber.d("ERROR: $e")
        }

        if (amountValue <= BigDecimal(0)) {
            binding.edtAmount.error = getString(R.string.text_must_be_greater_than_zero)
            return
        }

        if (passedTransaction == null) {
            val transaction = TransactionEntity().apply {
                description = descriptionValue
                amount = amountValue
                type = EXPENSE.ordinal
                payment = selectedPayment
                pattern = selectedPattern
                categoryId = selectedCategory
                subCategoryId = selectedSubCategory
                registerDate = selectedDate.time.time
            }
            viewModel.insertTransaction(transaction)
        } else {
            val transaction = passedTransaction?.apply {
                description = descriptionValue
                amount = amountValue
                payment = selectedPayment
                pattern = selectedPattern
                categoryId = selectedCategory
                subCategoryId = selectedSubCategory
                registerDate = selectedDate.time.time
            }
            transaction?.let {
                viewModel.updateTransaction(transaction)
            }
        }

        runDelayed(CLOSE_DELAY) { dismiss() }
    }

    private fun changeColors(pattern: Int) {
        val colorTo = ContextCompat.getColorStateList(
            requireContext(),
            Constants.patternColors[pattern]
        )
        val chipBackgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            Constants.patternChipBackgroundColorStateList[pattern]
        )
        val chipStrokeColor = ContextCompat.getColorStateList(
            requireContext(),
            Constants.patternChipStrokeColorStateList[pattern]
        )

        when {
            binding.chipNormal.isSelected -> {
                binding.chipNormal.isSelected = false
            }
            binding.chipWaste.isSelected -> {
                binding.chipWaste.isSelected = false
            }
            binding.chipInvest.isSelected -> {
                binding.chipInvest.isSelected = false
            }
        }

        when (pattern) {
            NORMAL.ordinal -> {
                binding.chipNormal.chipBackgroundColor = chipBackgroundColor
                binding.chipNormal.isSelected = true
            }
            WASTE.ordinal -> {
                binding.chipWaste.chipBackgroundColor = chipBackgroundColor
                binding.chipWaste.isSelected = true
            }
            INVEST.ordinal -> {
                binding.chipInvest.chipBackgroundColor = chipBackgroundColor
                binding.chipInvest.isSelected = true
            }
        }

        binding.txtDatePicked.setTextColor(colorTo)

        binding.chipToday.chipBackgroundColor = chipBackgroundColor
        binding.chipYesterday.chipBackgroundColor = chipBackgroundColor
        binding.chipPickDate.chipBackgroundColor = chipBackgroundColor

        binding.chipToday.chipStrokeColor = chipStrokeColor
        binding.chipYesterday.chipStrokeColor = chipStrokeColor
        binding.chipPickDate.chipStrokeColor = chipStrokeColor

        selectedPattern = pattern
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

        fun newInstance(transaction: TransactionEntity? = null, dateLong: Long? = null) = AddExpenseFragment().apply {
            arguments = Bundle().apply {
                putParcelable(TRANSACTION, transaction)
                dateLong?.let { putLong(DATE, it) }
            }
        }
    }


}