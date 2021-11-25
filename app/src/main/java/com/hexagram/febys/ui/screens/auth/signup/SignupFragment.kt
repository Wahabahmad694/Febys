package com.hexagram.febys.ui.screens.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentSignupBinding
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.auth.SocialMediaAuthFragment
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : SocialMediaAuthFragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: AuthViewModel by viewModels()
    private val args: SignupFragmentArgs by navArgs()

    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var countryCode: String = ""
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

        initUi()
        uiListeners()
        setupObserver()
    }

    private fun initUi() {
        binding.ccpPhoneCode.setDefaultCountryUsingNameCode("GH")
        binding.ccpPhoneCode.resetToDefaultCountry()
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
            if (args.isOpenFromLogin) {
                goBack()
                return@setOnClickListener
            }

            val gotoLogin = NavGraphDirections.actionToLoginFragment(true)
            navigateTo(gotoLogin)
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
            val countryCode = binding.ccpPhoneCode.selectedCountryCodeWithPlus
            phone = "$countryCode${it.toString()}"
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
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    gotoNextScreen()
                }
            }
        }

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
        val popUpTo = if (args.isOpenFromLogin) R.id.loginFragment else R.id.signupFragment

        if (isSocialLogin) {
            findNavController().popBackStack(popUpTo, true)
        } else {
            navigateToOTPVerification(popUpTo)
        }
    }

    private fun signup() {
        val requestSignup = createSignupRequest()
        viewModel.signup(requestSignup)
    }

    private fun createSignupRequest(): RequestSignup {
        val phoneCountryCode = binding.ccpPhoneCode.selectedCountryNameCode
        return RequestSignup(
            firstName, lastName, email, phone, phoneCountryCode, password
        )
    }

    private fun areAllFieldsValid(): Boolean {
        if (!Validator.isValidName(firstName)) {
            showErrorDialog(getString(R.string.error_enter_first_name))
            return false
        }

        if (!Validator.isValidName(lastName)) {
            showErrorDialog(getString(R.string.error_enter_last_name))
            return false
        }

        if (email.isEmpty()) {
            showErrorDialog(getString(R.string.error_enter_email))
            return false
        } else if (!Validator.isValidEmail(email)) {
            showErrorDialog(getString(R.string.error_enter_valid_email))
            return false
        }

        if (phone.isEmpty()) {
            showErrorDialog(getString(R.string.error_enter_phone))
            return false
        } else if (!Validator.isValidPhone(phone)) {
            showErrorDialog(getString(R.string.error_enter_valid_phone))
            return false
        }

        if (!Validator.isValidPassword(password)) {
            showErrorDialog(getString(R.string.error_enter_password))
            return false
        }

        if (confirmPassword.isEmpty()) {
            showErrorDialog(getString(R.string.error_enter_confirm_password))
            return false
        } else if (confirmPassword != password) {
            showErrorDialog(getString(R.string.error_confirm_password_not_match))
            return false
        }

        return true
    }

    private fun navigateToOTPVerification(popUpTo: Int) {
        val navigateToOtpVerification =
            SignupFragmentDirections.actionSignupFragmentToOtpVerificationDialog(popUpTo, false)
        navigateTo(navigateToOtpVerification)
    }
}