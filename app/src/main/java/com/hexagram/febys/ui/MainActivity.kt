package com.hexagram.febys.ui

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.braintreepayments.api.DropInResult
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseActivity
import com.hexagram.febys.broadcast.NotificationLocalBroadcastReceiver
import com.hexagram.febys.databinding.ActivityMainBinding
import com.hexagram.febys.ui.screens.home.HomeFragment
import com.hexagram.febys.ui.screens.payment.PaymentFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val DROP_IN_REQUEST_CODE = 0
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
            if (navController.currentDestination?.id == R.id.homeFragment) {
                (navHostFragment.childFragmentManager.fragments[0] as? HomeFragment)?.refreshData()
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val mapFragment = navFragment!!.childFragmentManager.primaryNavigationFragment


        if (requestCode == DROP_IN_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val result: DropInResult? =
                    data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                val paymentMethodNonce = result?.paymentMethodNonce?.string
                val paymentMethod = result?.paymentMethodType
                if (mapFragment is PaymentFragment) {
                    paymentMethodNonce?.let {
                        (mapFragment as PaymentFragment).createBraintreeTransaction(
                            it
                        )
                    }
                    Log.d("PaymentFragment1234567", "onCreate: XTXX")
                }
                Log.d(
                    "paymentMethodNonce",
                    "onActivityResult: $paymentMethodNonce : $paymentMethod"
                )
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // the user canceled
                Log.d("paymentMethodNonce", "RESULT_CANCELED: ")
            } else {
                // an error occurred, checked the returned exception
                val error: Exception? =
                    data?.getSerializableExtra(DropInResult.EXTRA_ERROR) as? Exception

                Log.d("paymentMethodNonce", "onActivityResult: $error")

            }
        }
    }
}