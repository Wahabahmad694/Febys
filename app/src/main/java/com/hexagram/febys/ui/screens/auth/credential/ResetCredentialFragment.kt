package com.hexagram.febys.ui.screens.auth.credential

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentResetCredentialBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.dialog.InfoDialog
import com.hexagram.febys.utils.Validator
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetCredentialFragment : com.hexagram.febys.base.BaseFragment() {
    private lateinit var binding: FragmentResetCredentialBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setupObserver()
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener {
            goBack()
        }

        binding.btnSendResetCredentialLink.setOnClickListener {
            val email = binding.etEmailAddress.text.toString()
            if (!Validator.isValidEmail(email)) {
                showToast(getString(R.string.error_enter_valid_email))
                return@setOnClickListener
            }
            viewModel.resetCredentials(email)
        }
    }

    private fun setupObserver() {
        viewModel.observeResetCredentialResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    InfoDialog(
                        R.drawable.ic_email,
                        getString(R.string.label_check_your_email),
                        getString(R.string.label_email_sent)
                    ).show(childFragmentManager, InfoDialog.TAG)
                }
            }
        }
    }
}