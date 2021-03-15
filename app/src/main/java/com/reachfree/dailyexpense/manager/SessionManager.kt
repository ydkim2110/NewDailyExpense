package com.reachfree.dailyexpense.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_CURRENCY_CODE
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_EVERY_DAY_NOTIFICATION
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_FULLNAME
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_IS_FIRST_LAUNCH
import com.reachfree.dailyexpense.util.Constants.PREF_KEY_NICKNAME
import com.reachfree.dailyexpense.util.PreferenceHelper
import com.reachfree.dailyexpense.util.PreferenceHelper.get

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-19
 * Time: 오전 11:00
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(context)

    companion object {
        const val USER_THEME = "user_theme"
    }

    fun getPrefs(): SharedPreferences = prefs

    fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        prefs.edit { putBoolean(PREF_KEY_IS_FIRST_LAUNCH, isFirstLaunch) }
    }

    fun getIsFirstLaunch(): Boolean {
        return prefs[PREF_KEY_IS_FIRST_LAUNCH, false]
    }

    fun saveCurrencyCode(code: String) {
        prefs.edit { putString(PREF_KEY_CURRENCY_CODE, code) }
    }

    fun getCurrencyCode(): String {
        return prefs[PREF_KEY_CURRENCY_CODE, Currency.USD.code]
    }

    fun saveUser(user: User) {
        prefs.edit {
            putString(PREF_KEY_NICKNAME, user.nickname)
            putString(PREF_KEY_FULLNAME, user.fullName)
        }
    }

    fun getUser(): User {
        return User().apply {
            nickname = prefs[PREF_KEY_NICKNAME, ""]
            fullName = prefs[PREF_KEY_FULLNAME, ""]
        }
    }

    fun setUserTheme(theme: Int) = prefs.edit { putInt(USER_THEME, theme) }

    fun getUserTheme(): Int = prefs[USER_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM]

    fun setEverydayNotification(isSet: Boolean) {
        prefs.edit { putBoolean(PREF_KEY_EVERY_DAY_NOTIFICATION, isSet) }
    }

    fun getEverydayNotification(): Boolean {
        return prefs[PREF_KEY_EVERY_DAY_NOTIFICATION, true]
    }

    fun removeUserTheme() = prefs.edit{ remove(USER_THEME) }
}