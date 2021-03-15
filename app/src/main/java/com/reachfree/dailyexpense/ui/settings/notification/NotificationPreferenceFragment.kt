package com.reachfree.dailyexpense.ui.settings.notification

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-15
 * Time: 오후 4:31
 */
const val EVERY_DAY_NOTIFICATION = "EVERY_DAY_NOTIFICATION"

@AndroidEntryPoint
class NotificationPreferenceFragment: PreferenceFragmentCompat() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_preferences, null)
        
        setupEverydayNotification()
    }

    private fun setupEverydayNotification() {
        val everydayNotification = preferenceManager.findPreference<Preference>(EVERY_DAY_NOTIFICATION)
        everydayNotification?.setDefaultValue(sessionManager.getEverydayNotification())

        everydayNotification?.setOnPreferenceChangeListener { preference, newValue ->
            sessionManager.setEverydayNotification(newValue as Boolean)
            true
        }
    }

}