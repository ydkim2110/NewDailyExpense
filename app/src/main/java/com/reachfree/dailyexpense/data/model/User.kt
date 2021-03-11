package com.reachfree.dailyexpense.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-25
 * Time: 오후 12:33
 */
@Parcelize
data class User(
    var nickname: String = "",
    var fullName: String = ""
): Parcelable