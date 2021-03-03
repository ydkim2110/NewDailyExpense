package com.reachfree.dailyexpense.util.extension

import android.widget.EditText

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-26
 * Time: 오전 11:06
 */
fun String.removeAfter2Decimal(edt: EditText) {
    return if (this.isNullOrEmpty() || this.isNullOrBlank() || this.toLowerCase() == "null") {

    } else {
        if (this.contains(".")) {
            var lastPartOfText = this.split(".")[this.split(".").size-1]

            if (lastPartOfText.count() > 2) {
                try {
                    lastPartOfText = this.substring(0, this.indexOf(".")+3)
                    edt.setText(lastPartOfText)
                    edt.setSelection(lastPartOfText.length)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

            }
        } else {

        }
    }
}