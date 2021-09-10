package com.hexagram.febys.ui.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAccountBinding
import com.hexagram.febys.network.response.User
import com.hexagram.febys.ui.screens.auth.AuthViewModel
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
        binding.btnSignIn.setOnClickListener {
            val navigateToLogin = AccountFragmentDirections.actionToLoginFragment()
            navigateTo(navigateToLogin)
        }

        binding.btnSignOut.setOnClickListener {
            authViewModel.signOut {
                super.signOut()
            }
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
    }

    private fun setupObserver() {
        observesUserLoggedIn.observe(viewLifecycleOwner) {
            val user = authViewModel.getUser()
            updateUserUi(user)
        }
    }

    private fun updateUserUi(user: User?) {
        binding.isUserLoggedIn = isUserLoggedIn
        binding.userName.text = user?.firstName ?: getString(R.string.app_name)
    }

    override fun getIvCart() = binding.ivCart
    override fun getTvCartCount() = binding.tvCartCount
}