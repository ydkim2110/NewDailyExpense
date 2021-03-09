package com.reachfree.dailyexpense

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.SessionManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오전 11:53
 */
@HiltAndroidApp
class MyApp : Application() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Constants.currencySymbol = Currency.fromCode(sessionManager.getCurrencyCode())?.symbol ?: Currency.USD.symbol

        Currency.fromCode(sessionManager.getCurrencyCode())?.let {
            Constants.currentCurrency = it
        }

        AppCompatDelegate.setDefaultNightMode(sessionManager.getUserTheme())
    }

}