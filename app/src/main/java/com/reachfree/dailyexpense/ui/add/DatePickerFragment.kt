package com.reachfree.dailyexpense.ui.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.reachfree.dailyexpense.R
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-17
 * Time: 오전 10:30
 */
class DatePickerFragment: DialogFragment(),
    DatePickerDialog.OnDateSetListener,
    DialogInterface.OnShowListener {

    var dateSelected: ((Int, Int, Int) -> Unit)? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(requireActivity(), this, year, month, dayOfMonth)
        dialog.setOnShowListener(this)
        return dialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateSelected?.invoke(year, month, dayOfMonth)
    }

    companion object {
        const val TAG = "DatePickerFragment"
        fun newInstance() = DatePickerFragment()
    }

    override fun onShow(dialog: DialogInterface?) {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
    }
}