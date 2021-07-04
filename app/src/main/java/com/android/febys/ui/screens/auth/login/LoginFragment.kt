package com.android.febys.ui.screens.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.febys.R
import com.android.febys.databinding.FragmentLoginBinding
import com.android.febys.enum.SocialLogin
import com.android.febys.network.DataState
import com.android.febys.ui.screens.auth.AuthFragment
import com.android.febys.ui.screens.auth.AuthViewModel
import com.android.febys.ui.screens.dialog.ErrorDialog
import com.android.febys.utils.Validator
import com.android.febys.utils.clearError
import com.android.febys.utils.navigateTo
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
            binding.etEmailAddress.clearError()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            binding.etPassword.clearError()
        }

        binding.tvGotoSignUp.setOnClickListener {
            val navigateToSignUp = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            navigateTo(navigateToSignUp)
        }

        binding.btnLogin.setOnClickListener {
            if (isAllFieldsAreValid()) {
                viewModel.login(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val navigateToResetCredential =
                LoginFragmentDirections.actionLoginFragmentToResetCredentialFragment()
            navigateTo(navigateToResetCredential)
        }

        binding.ivGoogle.setOnClickListener {
            signInWithGoogle { token ->
                viewModel.socialLogin(token, SocialLogin.GOOGLE)
            }
        }

        binding.ivFacebook.setOnClickListener {
            signInWithFacebook { token ->
                viewModel.socialLogin(token, SocialLogin.FACEBOOK)
            }
        }
    }


    private fun setupObserver() {
        viewModel.observeLoginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    findNavController().popBackStack(R.id.loginFragment, true)
                }
            }
        }
    }

    private fun isAllFieldsAreValid(): Boolean {
        var isFieldsValid = true

        if (email.isEmpty()) {
            binding.etEmailAddress.error = getString(R.string.error_enter_email)
            isFieldsValid = false
        } else if (!Validator.isValidEmail(email)) {
            binding.etEmailAddress.error = getString(R.string.error_enter_valid_email)
            isFieldsValid = false
        }

        if (!Validator.isValidPassword(password)) {
            binding.etPassword.error = getString(R.string.error_enter_password)
            isFieldsValid = false
        }

        return isFieldsValid
    }
}