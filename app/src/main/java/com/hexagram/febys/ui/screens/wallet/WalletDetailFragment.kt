package com.hexagram.febys.ui.screens.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWalletDetailBinding
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.TransactionPagerAdapter
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.vm.PaymentViewModel
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentWalletDetailBinding
    private val paymentViewModel by viewModels<PaymentViewModel>()
    private val transactionPagerAdapter = TransactionPagerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        refreshWallet()
        setupTransactionPagerAdapter()
    }

    private fun initUi() {
        binding.rvTransactions.isNestedScrollingEnabled = false
    }

    private fun setupTransactionPagerAdapter() {
        binding.rvTransactions.adapter = transactionPagerAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            paymentViewModel.allTransactions.collectLatest { pagingData ->
                transactionPagerAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            transactionPagerAdapter.loadStateFlow.collectLatest {
                val state = it.refresh

                if (state is LoadState.Error) {
                    showErrorToast(state)
                }

                val isEmpty =
                    it.refresh is LoadState.NotLoading && transactionPagerAdapter.itemCount < 1
                binding.emptyView.root.isVisible = isEmpty
                binding.rvTransactions.isVisible = !isEmpty

            }
        }
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
            tvAmount.text = wallet.getPrice().getFormattedPrice()
        }
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }
        binding.btnTopUp.setOnClickListener {
            val resId = R.drawable.ic_coming_soon
            val title = getString(R.string.label_coming_soon)
            val msg = getString(R.string.msg_for_coming_soon)

            showWarningDialog(resId, title, msg) {
                //todo top-up
            }
        }
        binding.btnWithDraw.setOnClickListener {
            val resId = R.drawable.ic_coming_soon
            val title = getString(R.string.label_coming_soon)
            val msg = getString(R.string.msg_for_coming_soon)

            showWarningDialog(resId, title, msg) {
                //todo withdraw amount
            }
        }
        binding.personName = consumer?.firstName?.split(" ")?.get(0) ?: getString(R.string.label_me)
    }
}