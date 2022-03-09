package com.hexagram.febys.ui

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseActivity
import com.hexagram.febys.broadcast.NotificationLocalBroadcastReceiver
import com.hexagram.febys.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val notificationBroadcastReceiver: BroadcastReceiver =
        NotificationLocalBroadcastReceiver { updateNotificationBadge() }

    private val showBottomNavInDestinations =
        listOf(
            R.id.homeFragment,
            R.id.notificationFragment,
            R.id.searchFragment,
            R.id.accountFragment,
            R.id.menuFragment
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        configureStatusBar()

        setContentView(binding.root)
        setupBottomNav()
    }

    private fun configureStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }

    private fun setupBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            // do not remove it, this is need to avoid recreation of fragment on reselect
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id in showBottomNavInDestinations
            if (destination.id == R.id.notificationFragment) clearBadge()
        }
    }

    private fun registerNotificationReceiver() {
        val filter =
            IntentFilter(NotificationLocalBroadcastReceiver.ACTION_RECEIVE_NOTIFICATION_BROADCAST)
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(notificationBroadcastReceiver, filter)
    }

    private fun unregisterNotificationReceiver() {
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(notificationBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerNotificationReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterNotificationReceiver()
    }

    private fun clearBadge() {
        pref.clearNotificationCount()
        updateNotificationBadge()
    }

    private fun updateNotificationBadge() {
        val number = pref.getNotificationCount()
        val notificationBadge = binding.bottomNavigation.getOrCreateBadge(R.id.notificationFragment)
        notificationBadge.isVisible = number > 0
        notificationBadge.number = number
    }
}