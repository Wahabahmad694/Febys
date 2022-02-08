package com.hexagram.febys.ui.screens.febysPlus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFebysPlusBinding
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.transaction.TransactionReq
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.auth.AuthViewModel
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.BasePaymentFragment
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FebysPlusFragment : BaseFragment() {
    private lateinit var binding: FragmentFebysPlusBinding
    private val febysPlusViewModel by viewModels<FebysPlusViewModel>()
    private val authViewModel: AuthViewModel by viewModels()
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
        authViewModel.fetchProfile()
    }

    private fun initUi() {
        binding.rvFebysPlus.adapter = febysPackageAdapter
        binding.rvFebysPlus.isNestedScrollingEnabled = false

        febysPackageAdapter.onItemClick = {
            if (isUserLoggedIn) {
                val paymentRequest =
                    PaymentRequest(it.price.value, it.price.currency, "SUBSCRIPTION_PURCHASED")
                val gotoPayment = NavGraphDirections.toPaymentFragment(paymentRequest)
                navigateTo(gotoPayment)
                showSuccessDialog()
                subscribePackage()
            } else gotoLogin()
        }
    }

    private fun updateUi(febysPackage: List<Package>) {
        val purchasedId = authViewModel.getSubscription()?.packageInfo?.id
        febysPackageAdapter.submitList(febysPackage, purchasedId)

    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }
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
        febysPlusViewModel.observerSubscription.observe(viewLifecycleOwner) {
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
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val resId = R.drawable.ic_thank
        val title = getString(R.string.msg_thank_you)
        val msg = getString(R.string.label_success_subscription)

        showInfoDialoge(resId, title, msg) { goBack() }
    }
    private fun subscribePackage() {
        setFragmentResultListener(BasePaymentFragment.TRANSACTIONS) { requestKey, bundle ->
            val transactions =
                bundle.getParcelableArrayList<Transaction>(BasePaymentFragment.TRANSACTIONS)
                    ?.map { it._id }!!
            febysPackageAdapter.getSelectedPkg()?.let {
                febysPlusViewModel.subscribePackage(it.id, TransactionReq(transactions))
            }
        }
    }

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

}
