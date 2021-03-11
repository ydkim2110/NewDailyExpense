package com.reachfree.dailyexpense.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.User
import com.reachfree.dailyexpense.databinding.SetupActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.dashboard.DashboardActivity
import com.reachfree.dailyexpense.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupActivity : BaseActivity<SetupActivityBinding>({ SetupActivityBinding.inflate(it) }),
    SetupFragment.OnGetStartedClickListener, DefaultSettingFragment.OnDefaultSettingDoneClicked,
    OnboardFragment.OnSetupFinished {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutSetup, SetupFragment())
                .commit()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.frameLayoutSetup, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onGetStartedClicked() {
        replaceFragment(DefaultSettingFragment.newInstance())
    }

    override fun onDefaultSettingDone(nickname: String, currency: Currency) {
        sessionManager.saveCurrencyCode(currency.code)
        sessionManager.saveUser(User(nickname))
        sessionManager.saveIsFirstLaunch(true)
        replaceFragment(OnboardFragment.newInstance())
    }

    override fun onSetupFinished() {
        //TODO: Save Done
        DashboardActivity.start(this)
        finish()
    }

    companion object {
        fun start(context: Context) {
            Intent(context, SetupActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }

}