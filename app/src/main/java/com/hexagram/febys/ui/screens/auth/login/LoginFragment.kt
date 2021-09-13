package com.hexagram.febys.ui.screens.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentLoginBinding
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.auth.SocialMediaAuthFragment
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : SocialMediaAuthFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private val args: LoginFragmentArgs by navArgs()

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
            if (args.isOpenFromSignup) {
                goBack()
                return@setOnClickListener
            }

            val navigateToSignUp = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            navigateTo(navigateToSignUp)
        }

        binding.btnLogin.setOnClickListener {
            if (areAllFieldsValid()) {
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
            val response = it.getContentIfNotHandled() ?: return@observe
            when (response) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(response).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    gotoNextScreen()
                }
            }
        }
    }

    private fun gotoNextScreen() {
        val popUpTo =
            if (args.isOpenFromSignup) R.id.signupFragment else R.id.loginFragment

        findNavController().popBackStack(popUpTo, true)
    }

    private fun areAllFieldsValid(): Boolean {
        if (email.isEmpty()) {
            showErrorDialog(getString(R.string.error_enter_email))
            return false
        } else if (!Validator.isValidEmail(email)) {
            showErrorDialog(getString(R.string.error_enter_valid_email))
            return false
        }

        if (!Validator.isValidPassword(password)) {
            showErrorDialog(getString(R.string.error_enter_password))
            return false
        }

        return true
    }
}