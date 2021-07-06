package com.hexagram.febys.ui.screens.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSplashBinding
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : com.hexagram.febys.base.BaseFragment() {
    private lateinit var binding: FragmentSplashBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        authViewModel.refreshToken()
    }

    private fun setupObservers() {
        authViewModel.observeRefreshTokenResponse.observe(viewLifecycleOwner) {
            val navigateToHome =
                SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            navigateTo(navigateToHome)
        }
    }
}