package com.reachfree.dailyexpense.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.reachfree.dailyexpense.ui.dashboard.DashboardActivity
import com.reachfree.dailyexpense.ui.setup.SetupActivity
import com.reachfree.dailyexpense.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.getIsFirstLaunch()) {
            SetupActivity.start(this)
        } else {
            DashboardActivity.start(this)
        }

    }

}