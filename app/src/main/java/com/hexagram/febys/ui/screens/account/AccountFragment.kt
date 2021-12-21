package com.hexagram.febys.ui.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAccountBinding
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : BaseFragment() {
    private lateinit var binding: FragmentAccountBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
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
            authViewModel.signOut {
                super.signOut()
            }
        }

        binding.orders.myOrders.setOnClickListener {
            gotoOrderListing(null,getString(R.string.label_my_orders))
        }

        binding.orders.orderReceived.setOnClickListener {
            gotoOrderListing(
                arrayOf(OrderStatus.DELIVERED),
                getString(R.string.label_order_received)
            )
        }

        binding.orders.cancelOrders.setOnClickListener {
            gotoOrderListing(
                arrayOf(
                    OrderStatus.CANCELED,
                    OrderStatus.CANCELLED_BY_VENDOR,
                    OrderStatus.REJECTED
                ),
                getString(R.string.label_cancel_orders)
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
           val goToAccountSettings = AccountFragmentDirections.actionAccountFragmentToAccountSettingsFragment()
           navigateTo(goToAccountSettings)
       }
    }

    private fun gotoOrderListing(status: Array<String>? = null, title: String? = null) {
        val navigateToOrderListing =
            AccountFragmentDirections.actionAccountFragmentToOrderListingFragment(status, title)
        navigateTo(navigateToOrderListing)
    }

    private fun setupObserver() {
        observesUserLoggedIn.observe(viewLifecycleOwner) {
            val user = authViewModel.getConsumer()
            updateUserUi(user)
        }
    }

    private fun updateUserUi(user: Consumer?) {
        binding.isUserLoggedIn = isUserLoggedIn
        binding.userName.text = user?.firstName?.split(" ")?.get(0) ?: getString(R.string.label_me)
    }

    override fun getIvCart() = binding.ivCart
    override fun getTvCartCount() = binding.tvCartCount
}