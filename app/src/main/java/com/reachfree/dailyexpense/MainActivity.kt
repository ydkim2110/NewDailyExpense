package com.reachfree.dailyexpense

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.reachfree.dailyexpense.databinding.MainActivityBinding
import com.reachfree.dailyexpense.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainActivityBinding>({ MainActivityBinding.inflate(it) }) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.fragment))
        binding.bottomNavigationView.setOnNavigationItemReselectedListener {}
    }

}