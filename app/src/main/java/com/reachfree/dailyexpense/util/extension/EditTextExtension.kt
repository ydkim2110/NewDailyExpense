package com.reachfree.dailyexpense.util.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-25
 * Time: 오전 10:25
 */
fun EditText.afterTextChanged(action: (Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            action(editable)
        }
    })
}