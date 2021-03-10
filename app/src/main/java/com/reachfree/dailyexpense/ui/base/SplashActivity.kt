package com.reachfree.dailyexpense.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.reachfree.dailyexpense.databinding.SplashActivityBinding
import com.reachfree.dailyexpense.ui.dashboard.DashboardActivity
import com.reachfree.dailyexpense.ui.setup.SetupActivity
import com.reachfree.dailyexpense.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<SplashActivityBinding>({ SplashActivityBinding.inflate(it) }) {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            delay(500)
            binding.motionLayout.transitionToEnd()
            delay(700)
            if (!sessionManager.getIsFirstLaunch()) {
                SetupActivity.start(this@SplashActivity)
            } else {
                DashboardActivity.start(this@SplashActivity)
            }
            finish()
        }
    }

}