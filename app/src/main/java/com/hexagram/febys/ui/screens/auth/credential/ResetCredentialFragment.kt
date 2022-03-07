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
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetCredentialFragment : BaseFragment() {
    private lateinit var binding: FragmentResetCredentialBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
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
                showWarning()
                return@setOnClickListener
            }
            viewModel.resetCredentials(email)
        }
    }
    private fun showWarning() {
        val resId = R.drawable.ic_error
        val title = getString(R.string.label_try_again)
        val msg = getString(R.string.error_enter_valid_email)

        showInfoDialoge(resId, title, msg) { goBack() }
    }

    private fun setupObserver() {
        viewModel.observeResetCredentialResponse.observe(viewLifecycleOwner) {
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
                    InfoDialog(
                        R.drawable.ic_email,
                        getString(R.string.label_check_your_email),
                        getString(R.string.label_email_sent)
                    ) {
                       goBack()
                    }.show(childFragmentManager, InfoDialog.TAG)
                }
            }
        }
    }
}