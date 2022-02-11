package com.hexagram.febys.ui.screens.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentNotificationBinding
import com.hexagram.febys.models.api.notification.Notification
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationAdapter = NotificationAdapter()
    private val notificationViewModel by viewModels<NotificationViewModel>()

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
        setObservers()
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

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            notificationViewModel.fetchNotificationList().collectLatest {
                notificationAdapter.submitData(it)
            }
        }
    }

    private fun Notification.FebysPlus.navigate() {
        val actionToFebysPlus =
            NotificationFragmentDirections.actionNotificationFragmentToFebysPlusFragment()
        navigateTo(actionToFebysPlus)
    }

    private fun Notification.Order.navigate() {
        val actionToFebysPlus =
            NotificationFragmentDirections
                .actionNotificationFragmentToOrderDetailFragment(
                    null,
                    orderId,
                    status == OrderStatus.REVIEWED
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