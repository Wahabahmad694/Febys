package com.hexagram.febys.ui.screens.febysPlus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFebysPlusBinding
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.showLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FebysPlusFragment : BaseFragment() {
    private lateinit var binding: FragmentFebysPlusBinding
    private val febysPlusViewModel by viewModels<FebysPlusViewModel>()
    private val febysPackageAdapter = FebysPlusAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFebysPlusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvFebysPlus.adapter = febysPackageAdapter
        binding.rvFebysPlus.isNestedScrollingEnabled = false

        febysPackageAdapter.onItemClick = {
            val paymentRequest = PaymentRequest(it.price.value, it.price.currency, "SUBSCRIPTION_PURCHASED")
            val gotoPayment = NavGraphDirections.toPaymentFragment(paymentRequest)
            navigateTo(gotoPayment)
        }
    }

    private fun setObserver() {
        febysPlusViewModel.observeFebysPackage.observe(viewLifecycleOwner) {
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

    private fun updateUi(febysPackage: List<Package>) {
        febysPackageAdapter.submitList(febysPackage)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

    }
}
