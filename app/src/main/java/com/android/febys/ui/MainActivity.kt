package com.android.febys.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.febys.R
import com.android.febys.databinding.ActivityMainBinding
import com.android.febys.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val showBottomNavInDestinations =
        listOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.productDetailFragment /* todo remove this destination once flow implemented */
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            // do not remove it, this is need to avoid recreation of fragment on reselect
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id in showBottomNavInDestinations
        }
    }
}