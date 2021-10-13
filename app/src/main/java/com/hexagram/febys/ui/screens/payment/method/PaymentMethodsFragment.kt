package com.hexagram.febys.ui.screens.payment.method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentPaymentMethodsBinding
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodsFragment : BaseFragment() {
    private lateinit var binding: FragmentPaymentMethodsBinding
    private val paymentMethodViewModel by viewModels<PaymentMethodViewModel>()

    private val paymentMethodAdapter = PaymentMethodAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setObservers()
    }

    private fun initUi() {
        binding.rvPaymentMethod.adapter = paymentMethodAdapter
    }

    private fun uiListeners() {
        paymentMethodAdapter.setAsDefault = {
            paymentMethodViewModel.setAsDefault(it)
        }

        binding.ivBack.setOnClickListener { goBack() }
        binding.btnSave.setOnClickListener { goBack() }
    }

    private fun setObservers() {
        paymentMethodViewModel.observePaymentMethods.observe(viewLifecycleOwner) {
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
                    updateUi(it.data)
                }
            }
        }
    }

    private fun updateUi(paymentMethods: List<PaymentMethod>) {
        paymentMethodAdapter.submitList(paymentMethods)
    }
}