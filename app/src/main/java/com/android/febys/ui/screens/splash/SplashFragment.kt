package com.android.febys.ui.screens.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.febys.databinding.FragmentSplashBinding
import com.android.febys.ui.base.BaseFragment
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
            val actionToHome = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            findNavController().navigate(actionToHome)
        }
    }
}