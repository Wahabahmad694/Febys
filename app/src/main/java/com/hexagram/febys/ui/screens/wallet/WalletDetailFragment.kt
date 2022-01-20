package com.hexagram.febys.ui.screens.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWalletDetailBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.vm.PaymentViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.hexagram.febys.utils.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentWalletDetailBinding
    private val paymentViewModel by viewModels<PaymentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshWallet()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()
        setObserver()
    }

    private fun setObserver() {

    }

    private fun refreshWallet() {
        paymentViewModel.refreshWallet().observe(viewLifecycleOwner) {
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
                    updateWalletUi(it.data)
                }
            }
        }
    }

    private fun updateWalletUi(wallet: Wallet) {
        binding.apply {
            tvAmount.text = Price("", wallet.amount, wallet.currency).getFormattedPrice()
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }
        binding.btnTopUp.setOnClickListener {
            val resId = R.drawable.ic_wallet
            val title = getString(R.string.label_delete_warning)
            val msg = getString(R.string.msg_for_withdraw)

            showWarningDialog(resId, title, msg) {
                //todo nothing
            }
        }
        binding.btnWithDraw.setOnClickListener {
            val resId = R.drawable.ic_wallet
            val title = getString(R.string.label_delete_warning)
            val msg = getString(R.string.msg_for_withdraw)

            showWarningDialog(resId, title, msg) {
                //todo nothing
            }
        }
        binding.personName = consumer?.firstName?.split(" ")?.get(0) ?: getString(R.string.label_me)
    }
}