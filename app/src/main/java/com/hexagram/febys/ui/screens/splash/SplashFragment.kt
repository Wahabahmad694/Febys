package com.hexagram.febys.ui.screens.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSplashBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
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
        authViewModel.fetchProfile()
    }

    private fun setupObservers() {
        authViewModel.observeProfileResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.NetworkError -> {
                    val closeAppCallback = { closeApp() }
                    ErrorDialog(
                        it, onOkayClick = closeAppCallback, onCloseClick = closeAppCallback
                    ).show(childFragmentManager, ErrorDialog.TAG)
                }
                else -> {
                    val navigateToHome =
                        SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                    navigateTo(navigateToHome)

                    findNavController().graph.startDestination = R.id.homeFragment
                }
            }
        }
    }

    private fun closeApp() {
        exitProcess(0)
    }
}