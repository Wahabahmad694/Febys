package com.android.febys.ui.screens.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.android.febys.R
import com.android.febys.databinding.FragmentLoginBinding
import com.android.febys.network.DataState
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseSignup
import com.android.febys.ui.screens.auth.AuthFragment
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : AuthFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    private var email = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
        binding.etEmailAddress.addTextChangedListener {
            email = it.toString()
            binding.tilEmailAddress.clearError()
            if (email.isEmpty()) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_email)
            } else if (!Validator.isValidEmail(email)) {
                binding.tilEmailAddress.error = getString(R.string.error_enter_valid_email)
            }

            updateLoginButtonVisibility()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            binding.tilPassword.clearError()
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.error_enter_password)
            }

            updateLoginButtonVisibility()
        }


        binding.tvGotoSignUp.setOnClickListener {
            val navigateToSignUp = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            navigateTo(navigateToSignUp)
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            val navigateToResetCredential =
                LoginFragmentDirections.actionLoginFragmentToResetCredentialFragment()
            navigateTo(navigateToResetCredential)
        }
    }


    private fun setupObserver() {
        viewModel.observeLoginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    val msg = getErrorMessage(it)
                    showToast(msg)
                }
                is DataState.Data -> {
                    handleResponse(it.data)
                }
            }
        }
    }

    private fun handleResponse(response: ResponseLogin) {
        when (response) {
            is ResponseLogin.Fail -> {
                val error = response.message
                showToast(error)
            }
            is ResponseLogin.Success -> {
                navigateToHomeScreen()
            }
        }
    }

    private fun navigateToHomeScreen() {
        val navigateToHomeScreen = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        navigateTo(navigateToHomeScreen)
    }

    private fun updateLoginButtonVisibility() {
        var enableButton = true

        if (!Validator.isValidEmail(email)) {
            enableButton = false
        }

        if (!Validator.isValidPassword(password)) {
            enableButton = false
        }

        binding.btnLogin.isEnabled = enableButton
    }
}