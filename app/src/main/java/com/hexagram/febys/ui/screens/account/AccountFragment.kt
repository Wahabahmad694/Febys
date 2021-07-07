package com.hexagram.febys.ui.screens.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAccountBinding
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : com.hexagram.febys.base.BaseFragment() {
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

    private fun setupObserver() {
        observesUserLoggedIn.observe(viewLifecycleOwner) {
            binding.isUserLoggedIn = it
        }
    }

    private fun uiListeners() {
        binding.btnSignIn.setOnClickListener {
            authViewModel.signOut {
                val navigateToLogin =
                    AccountFragmentDirections.actionAccountFragmentToLoginFragment()
                navigateTo(navigateToLogin)
            }
        }

        binding.btnSignOut.setOnClickListener {
            authViewModel.signOut {
                super.signOut()
            }
        }
    }
}