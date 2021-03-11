package com.reachfree.dailyexpense.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.ui.settings.backup.BackupFragment
import com.reachfree.dailyexpense.ui.settings.profile.ProfileFragment
import com.reachfree.dailyexpense.ui.setup.currency.CurrencyFragment
import com.reachfree.dailyexpense.util.Preferences.THEME_ARRAY
import com.reachfree.dailyexpense.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-26
 * Time: 오전 11:56
 */

const val PREF_PROFILE = "PROFILE"
const val PREF_CURRENCY = "CURRENCY"
const val PREF_APP_THEME = "APP_THEME"
const val PREF_BACKUP = "BACKUP"

@AndroidEntryPoint
class PrefFragment: PreferenceFragmentCompat() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setupProfilePreference()
        setupCurrencyPreference()
        setupAppThemePreference()
        setupBackupPreference()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupProfilePreference() {
        val profilePref = preferenceManager.findPreference<Preference>(PREF_PROFILE)
        profilePref?.summary = sessionManager.getUser().nickname

        profilePref?.setOnPreferenceClickListener { profile ->
            val profileFragment = ProfileFragment.newInstance()
            profileFragment.show(childFragmentManager, null)

            childFragmentManager.setFragmentResultListener("profile", this) { key, bundle ->
                val result = bundle.getParcelable<User>("data")

                result?.let {
                    profile.summary = it.nickname
                    sessionManager.saveUser(it)
                }
            }

            true
        }
    }

    private fun setupCurrencyPreference() {
        val currencyPref = preferenceManager.findPreference<Preference>(PREF_CURRENCY)
        currencyPref?.summary = Currency.fromCode(sessionManager.getCurrencyCode())?.flag

        currencyPref?.setOnPreferenceClickListener { currency ->
            val currencyFragment = CurrencyFragment.newInstance()
            currencyFragment.show(childFragmentManager, null)

            childFragmentManager.setFragmentResultListener("currency", this) { key, bundle ->
                val result = bundle.getParcelable<Currency>("data")
                result?.let {
                    currency.summary = Currency.fromCode(it.code)?.flag
                    sessionManager.saveCurrencyCode(it.code)
                }
            }

            true
        }
    }

    private fun setupAppThemePreference() {
        val appThemePref = preferenceManager.findPreference<Preference>(PREF_APP_THEME)
        val currentTheme = sessionManager.getUserTheme()
        val appTheme = THEME_ARRAY.first { it.modeNight == currentTheme }
        appThemePref?.summary = getString(appTheme.modeNameRes)

        appThemePref?.setOnPreferenceChangeListener { preference, newValue ->
            val mode = THEME_ARRAY[newValue.toString().toInt()].modeNight
            AppCompatDelegate.setDefaultNightMode(mode)
            sessionManager.setUserTheme(mode)
            preference.summary = getString(THEME_ARRAY.first { it.modeNight == sessionManager.getUserTheme() }.modeNameRes)
            true
        }
    }

    private fun setupBackupPreference() {
        val backupPref = preferenceManager.findPreference<Preference>(PREF_BACKUP)

        backupPref?.setOnPreferenceClickListener {
            val backupFragment = BackupFragment.newInstance()
            backupFragment.show(childFragmentManager, null)

            true
        }
    }
}