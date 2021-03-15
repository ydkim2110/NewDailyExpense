package com.reachfree.dailyexpense.ui.settings.notification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.NotificationActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import com.reachfree.dailyexpense.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<NotificationActivityBinding>({ NotificationActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbarTitle.text = getString(R.string.toolbar_title_notification)
    }

    companion object {
        fun start(context: Context) {
            Intent(context, NotificationActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }
}