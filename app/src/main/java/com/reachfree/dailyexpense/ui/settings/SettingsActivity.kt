package com.reachfree.dailyexpense.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.databinding.SettingsActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<SettingsActivityBinding>({ SettingsActivityBinding.inflate(it) }) {

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
        binding.toolbarTitle.text = "Settings"
    }

    companion object {
        fun start(context: Context) {
            Intent(context, SettingsActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }
}