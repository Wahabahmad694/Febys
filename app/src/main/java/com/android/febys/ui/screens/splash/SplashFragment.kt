package com.android.febys.ui.screens.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.android.febys.databinding.FragmentSplashBinding
import com.android.febys.base.BaseFragment
import com.android.febys.utils.navigateTo
import kotlinx.coroutines.delay

class SplashFragment : BaseFragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            delay(2000)
            // todo navigate by checking auth token
            // this navigation is for testing purpose
            val navigateToLogin = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            navigateTo(navigateToLogin)
        }
    }
}