package com.hexagram.febys.ui.screens.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentSignupBinding
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.auth.SocialMediaAuthFragment
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.Validator
import com.hexagram.febys.utils.clearError
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : SocialMediaAuthFragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: AuthViewModel by viewModels()

    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""

    private var isSocialLogin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
        addTextChangeListenerOnFields()

        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnSignUp.setOnClickListener {
            isSocialLogin = false
            if (areAllFieldsValid()) {
                signup()
            }
        }

        binding.tvGotoLogin.setOnClickListener {
            goBack()
        }

        binding.ivGoogle.setOnClickListener {
            signInWithGoogle { token ->
                viewModel.socialLogin(token, SocialLogin.GOOGLE)
                isSocialLogin = true
            }
        }

        binding.ivFacebook.setOnClickListener {
            signInWithFacebook { token ->
                viewModel.socialLogin(token, SocialLogin.FACEBOOK)
                isSocialLogin = true
            }
        }
    }

    private fun addTextChangeListenerOnFields() {
        binding.etFirstName.addTextChangedListener {
            firstName = it.toString()
            binding.etFirstName.clearError()
        }

        binding.etLastName.addTextChangedListener {
            lastName = it.toString()
            binding.etLastName.clearError()
        }

        binding.etEmailAddress.addTextChangedListener {
            email = it.toString()
            binding.etEmailAddress.clearError()
        }

        binding.etPhone.addTextChangedListener {
            phone = it.toString()
            binding.etPhone.clearError()
        }

        binding.etPassword.addTextChangedListener {
            password = it.toString()
            binding.etPassword.clearError()
        }

        binding.etConfirmPassword.addTextChangedListener {
            confirmPassword = it.toString()
            binding.etConfirmPassword.clearError()
        }
    }

    private fun setupObserver() {
        viewModel.observeSignupResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    gotoNextScreen()
                }
            }
        }
    }

    private fun gotoNextScreen() {
        if (isSocialLogin) {
            findNavController().popBackStack(R.id.loginFragment, true)
        } else {
            navigateToOTPVerification()
        }
    }

    private fun signup() {
        val requestSignup = createSignupRequest()

        viewModel.signup(requestSignup)
    }

    private fun createSignupRequest(): RequestSignup {
        return RequestSignup(
            firstName, lastName, email, phone, password
        )
    }

    private fun areAllFieldsValid(): Boolean {
        var areAllFieldsValid = true

        if (!Validator.isValidName(firstName)) {
            binding.etFirstName.error = getString(R.string.error_enter_first_name)
            areAllFieldsValid = false
        }

        if (!Validator.isValidName(lastName)) {
            binding.etLastName.error = getString(R.string.error_enter_last_name)
            areAllFieldsValid = false
        }

        if (email.isEmpty()) {
            binding.etEmailAddress.error = getString(R.string.error_enter_email)
            areAllFieldsValid = false
        } else if (!Validator.isValidEmail(email)) {
            binding.etEmailAddress.error = getString(R.string.error_enter_valid_email)
            areAllFieldsValid = false
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = getString(R.string.error_enter_phone)
            areAllFieldsValid = false
        } else if (!Validator.isValidPhone(phone)) {
            binding.etPhone.error = getString(R.string.error_enter_valid_phone)
            areAllFieldsValid = false
        }

        if (!Validator.isValidPassword(password)) {
            binding.etPassword.error = getString(R.string.error_enter_password)
            areAllFieldsValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = getString(R.string.error_enter_confirm_password)
            areAllFieldsValid = false
        } else if (confirmPassword != password) {
            binding.etConfirmPassword.error = getString(R.string.error_confirm_password_not_match)
            areAllFieldsValid = false
        }

        return areAllFieldsValid
    }

    private fun navigateToOTPVerification() {
        val navigateToOtpVerification =
            SignupFragmentDirections.actionSignupFragmentToOtpVerificationDialog(false)
        navigateTo(navigateToOtpVerification)
    }
}