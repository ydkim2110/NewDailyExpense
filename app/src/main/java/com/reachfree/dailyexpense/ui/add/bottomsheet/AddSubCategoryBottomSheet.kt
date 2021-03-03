package com.reachfree.dailyexpense.ui.add.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.SubCategory
import com.reachfree.dailyexpense.databinding.AddCategoryBottomSheetBinding
import com.reachfree.dailyexpense.databinding.TransactionListBottomSheetBinding
import com.reachfree.dailyexpense.util.AppUtils
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.Status
import com.reachfree.dailyexpense.util.Constants.TYPE
import com.reachfree.dailyexpense.util.extension.setOnSingleClickListener
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 9:51
 */
@AndroidEntryPoint
class AddSubCategoryBottomSheet(
    private val category: Category
) : BottomSheetDialogFragment() {

    private var _binding: AddCategoryBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val addSubCategoryAdapter: AddSubCategoryAdapter by lazy {
        AddSubCategoryAdapter()
    }

    private val subCategories by lazy {
        Constants.expenseSubCategories().filter { it.categoryId == category.id }
    }

    private lateinit var onSubCategoryItemSelected: OnSubCategoryItemSelected

    fun setOnSubCategoryItemSelected(onSubCategoryItemSelected: OnSubCategoryItemSelected) {
        this.onSubCategoryItemSelected = onSubCategoryItemSelected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SelectTypeBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCloseIcon.setOnSingleClickListener {
            dismiss()
        }

        binding.recyclerCategory.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = addSubCategoryAdapter
            addSubCategoryAdapter.submitList(subCategories)
        }

        addSubCategoryAdapter.setOnItemClickListener { subCategory ->
            onSubCategoryItemSelected.onSelectedSubCategory(subCategory)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnSubCategoryItemSelected {
        fun onSelectedSubCategory(subCategory: SubCategory)
    }

    companion object {
        const val TAG = "AddSubCategoryBottomSheet"
    }
}