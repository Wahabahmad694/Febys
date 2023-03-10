package com.hexagram.febys.ui.screens.notifications

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.broadcast.NotificationLocalBroadcastReceiver
import com.hexagram.febys.databinding.FragmentNotificationBinding
import com.hexagram.febys.models.api.notification.Notification
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationAdapter = NotificationAdapter()
    private val notificationViewModel by viewModels<NotificationViewModel>()
    private val notificationBroadcastReceiver: BroadcastReceiver =
        NotificationLocalBroadcastReceiver {
            lifecycleScope.launch {
                delay(2000)
                fetchNotification(true)
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setupOrderPagerAdapter()
        setObserver()
    }

    private fun setObserver() {
        observesUserLoggedIn.observe(viewLifecycleOwner) {
            binding.isUserLoggedIn = it
            if (it) {
                fetchNotification()
            }
        }
    }

    private fun initUi() {
        binding.rvNotification.adapter = notificationAdapter
    }

    private fun uiListeners() {
        notificationAdapter.onItemClick = ItemClick@{
            val notification = it.getNotification() ?: return@ItemClick
            when (notification) {
                is Notification.FebysPlus -> {
                    notification.navigate()
                }
                is Notification.Order -> {
                    notification.navigate()
                }
                is Notification.QA -> {
                    notification.navigate()
                }
                is Notification.Consumer -> {
                    notification.navigate()
                }
            }
            notificationViewModel.markRead(it._id)
        }

        binding.gotoLogin.setOnClickListener {
            val navigateToLogin = NavGraphDirections.actionToLoginFragment()
            navigateTo(navigateToLogin)
        }
    }

    private fun setupOrderPagerAdapter() {
        binding.rvNotification.adapter = notificationAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            notificationAdapter.loadStateFlow.collectLatest {
                val state = it.refresh
                if (state is LoadState.Loading) {
                    showLoader()
                } else {
                    hideLoader()
                }

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }
                binding.emptyView.root.isVisible =
                    it.refresh is LoadState.NotLoading && notificationAdapter.itemCount < 1
            }
        }
    }

    private fun fetchNotification(refresh: Boolean = false) {
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.fetchNotificationList(refresh).collectLatest {
                notificationAdapter.submitData(it)
            }
        }
    }

    private fun registerNotificationReceiver() {
        val filter =
            IntentFilter(NotificationLocalBroadcastReceiver.ACTION_RECEIVE_NOTIFICATION_BROADCAST)
        LocalBroadcastManager
            .getInstance(requireContext())
            .registerReceiver(notificationBroadcastReceiver, filter)
    }

    private fun unregisterNotificationReceiver() {
        LocalBroadcastManager
            .getInstance(requireContext())
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

    private fun Notification.FebysPlus.navigate() {
        val actionToFebysPlus =
            NotificationFragmentDirections.actionNotificationFragmentToFebysPlusFragment()
        navigateTo(actionToFebysPlus)
    }

    private fun Notification.Order.navigate() {
        fun getOrderDetail(status: String): String {
            return when (status) {
                OrderStatus.DELIVERED -> {
                    getString(R.string.label_received_details)
                }
                OrderStatus.REVIEWED -> {
                    getString(R.string.label_my_review)
                }
                OrderStatus.CANCELED,
                OrderStatus.CANCELLED_BY_VENDOR,
                OrderStatus.REJECTED -> {
                    getString(R.string.label_cancel_details)
                }
                OrderStatus.RETURNED,
                OrderStatus.PENDING_RETURN -> {
                    getString(R.string.label_return_details)
                }
                else -> {
                    getString(R.string.label_order_detail)
                }
            }
        }

        val actionToFebysPlus =
            NotificationFragmentDirections
                .actionNotificationFragmentToOrderDetailFragment(
                    getOrderDetail(status),
                    orderId,
                    status == OrderStatus.REVIEWED,
                    status in arrayOf(OrderStatus.RETURNED, OrderStatus.PENDING_RETURN)
                )
        navigateTo(actionToFebysPlus)
    }

    private fun Notification.QA.navigate() {
        val actionToFebysPlus =
            NotificationFragmentDirections.actionNotificationFragmentToProductDetailFragment(
                productId, "", threadId
            )
        navigateTo(actionToFebysPlus)
    }

    private fun Notification.Consumer.navigate() {
        val actionToAccountSettings =
            NotificationFragmentDirections.actionNotificationFragmentToAccountSettingsFragment()
        navigateTo(actionToAccountSettings)
    }
}