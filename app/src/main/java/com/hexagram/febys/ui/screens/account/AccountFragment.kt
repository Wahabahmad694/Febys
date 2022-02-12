package com.hexagram.febys.ui.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hexagram.febys.BuildConfig
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAccountBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.notification.FirebaseUtils
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.load
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseFragment() {
    private lateinit var binding: FragmentAccountBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setupObserver()
        authViewModel.fetchProfile()
    }

    private fun initUi() {
        updateWalletUi()
    }

    private fun uiListeners() {
        binding.settings.toggleNotification.setOnCheckedChangeListener { _, isChecked ->
            toggleNotification(isChecked)
        }
        binding.orders.walletImg.setImageResource(R.drawable.ic_wallet)
        binding.btnLogin.setOnClickListener {
            val navigateToLogin = AccountFragmentDirections.actionToLoginFragment()
            navigateTo(navigateToLogin)
        }

        binding.btnCreateAccount.setOnClickListener {
            val navigateToSignup =
                AccountFragmentDirections.actionAccountFragmentToSignupFragment(false)
            navigateTo(navigateToSignup)
        }

        binding.btnSignOut.setOnClickListener {
            authViewModel.signOut { signOut() }
        }
        binding.orders.containerWallet.setOnClickListener {
            val gotoWallet = AccountFragmentDirections.actionAccountFragmentToWalletFragment()
            navigateTo(gotoWallet)
        }

        binding.orders.myOrders.setOnClickListener {
            gotoOrderListing(
                arrayOf(
                    OrderStatus.PENDING,
                    OrderStatus.ACCEPTED,
                    OrderStatus.SHIPPED
                ),
                getString(R.string.label_my_orders),
                getString(R.string.label_order_detail),
            )
        }

        binding.orders.orderReceived.setOnClickListener {
            gotoOrderListing(
                arrayOf(OrderStatus.DELIVERED),
                getString(R.string.label_order_received),
                getString(R.string.label_received_details)
            )
        }

        binding.orders.myReview.setOnClickListener {
            gotoOrderListing(
                arrayOf(OrderStatus.REVIEWED),
                getString(R.string.label_my_reviews),
                getString(R.string.label_my_review)
            )
        }

        binding.orders.cancelOrders.setOnClickListener {
            gotoOrderListing(
                arrayOf(
                    OrderStatus.CANCELED,
                    OrderStatus.CANCELLED_BY_VENDOR,
                    OrderStatus.REJECTED
                ),
                getString(R.string.label_cancel_orders),
                getString(R.string.label_cancel_details)
            )
        }

        binding.orders.returnOrders.setOnClickListener {
            gotoOrderListing(
                arrayOf(
                    OrderStatus.RETURNED,
                    OrderStatus.PENDING_RETURN
                ),
                getString(R.string.label_return_orders),
                getString(R.string.label_return_details)
            )
        }

        binding.orders.wishlist.setOnClickListener {
            val navigateToWishlist =
                AccountFragmentDirections.actionAccountFragmentToWishListFragment()
            navigateTo(navigateToWishlist)
        }

        binding.settings.shippingAddress.setOnClickListener {
            val shippingAddress =
                AccountFragmentDirections.actionAccountFragmentToShippingFragment()
            navigateTo(shippingAddress)
        }

        binding.settings.vouchers.setOnClickListener {
            val gotoVouchers = AccountFragmentDirections.actionAccountFragmentToVouchersFragment()
            navigateTo(gotoVouchers)
        }
        binding.settings.accountSettings.setOnClickListener {
            val goToAccountSettings =
                AccountFragmentDirections.actionAccountFragmentToAccountSettingsFragment()
            navigateTo(goToAccountSettings)
        }
        binding.support.aboutFebys.setOnClickListener {
            val goToAboutFebys =
                NavGraphDirections.toWebViewFragment(getString(R.string.label_about_febys),
                    "${BuildConfig.backendBaseUrl}static/about-us",
                    false)
            navigateTo(goToAboutFebys)
        }
        binding.support.helpCenter.setOnClickListener {
            val goToHelpCenter =
                NavGraphDirections.toWebViewFragment(getString(R.string.label_help_center),
                    "${BuildConfig.backendBaseUrl}static/help-center",
                    false)
            navigateTo(goToHelpCenter)
        }
        binding.support.privacyPolicy.setOnClickListener {
            val goToPrivacyPolicy =
                NavGraphDirections.toWebViewFragment(getString(R.string.label_privacy_policy),
                    "${BuildConfig.backendBaseUrl}static/privacy-policy",
                    false)
            navigateTo(goToPrivacyPolicy)
        }
        binding.support.termsAndConditions.setOnClickListener {
            val goToTermsAndConditions =
                NavGraphDirections.toWebViewFragment(getString(R.string.label_terms_amp_conditions),
                    "${BuildConfig.backendBaseUrl}static/terms-and-conditions",
                    false)
            navigateTo(goToTermsAndConditions)
        }
    }

    private fun toggleNotification(notify: Boolean) {
        val topic = consumer?.id?.toString() ?: return
        if (notify) {
            FirebaseUtils.subscribeToTopic(topic)
        } else {
            FirebaseUtils.unSubscribeToTopic(topic)
        }
        authViewModel.updateNotificationSetting(notify)
    }

    private fun gotoOrderListing(
        status: Array<String>? = null, title: String? = null, titleForDetail: String? = null,
    ) {
        val navigateToOrderListing = AccountFragmentDirections
            .actionAccountFragmentToOrderListingFragment(status, title, titleForDetail)
        navigateTo(navigateToOrderListing)
    }

    private fun setupObserver() {
        observesUserLoggedIn.observe(viewLifecycleOwner) {
            updateUserUi()
            updateWalletUi()
        }

        authViewModel.observeProfileResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // no need to show loader
                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    updateUserUi()
                    updateWalletUi()
                }
            }
        }
    }

    private fun updateWalletUi() {
        val wallet = authViewModel.getWallet()
        binding.orders.labelPrice.text = wallet?.getPrice()?.getFormattedPrice()
    }

    private fun updateUserUi() {
        val user = authViewModel.getConsumer()
        binding.isUserLoggedIn = isUserLoggedIn
        binding.userName.text = user?.firstName?.split(" ")?.get(0) ?: getString(R.string.label_me)

        val subscription = authViewModel.getSubscription()
        if (subscription != null) {
            binding.icSubscription.load(subscription.packageInfo.icon)
        } else {
            binding.icSubscription.isVisible = false
        }

        if (isUserLoggedIn) {
            binding.settings.toggleNotification.isChecked = authViewModel.getNotificationSetting()
            binding.settings.toggleNotification.isEnabled = true
        } else {
            binding.settings.toggleNotification.isChecked = false
            binding.settings.toggleNotification.isEnabled = false
        }
    }

    override fun getIvCart() = binding.ivCart
    override fun getTvCartCount() = binding.tvCartCount
}