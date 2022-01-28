package com.hexagram.febys.ui.screens.vouchers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentVouchersBinding
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VouchersFragment : BaseFragment() {
    private lateinit var binding: FragmentVouchersBinding
    private val voucherViewModel by viewModels<VouchersViewModel>()

    private val vouchersAdapter = VouchersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        setObserver()
    }

    private fun initUi() {
        binding.rvVouchers.adapter = vouchersAdapter
        binding.rvVouchers.applySpaceItemDecoration(R.dimen._4sdp)
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.btnAddVoucher.setOnClickListener {
            val gotoAddVoucher =
                VouchersFragmentDirections.actionVoucherFragmentToAddVoucherBottomSheet()
            navigateTo(gotoAddVoucher)
        }

        setFragmentResultListener(AddVoucherBottomSheet.REQ_KEY_ADD_VOUCHER) { _, bundle ->
            val voucher = bundle.getString(AddVoucherBottomSheet.REQ_KEY_ADD_VOUCHER)
            voucher?.let { voucherViewModel.collectVoucher(it) }
        }
    }

    private fun setObserver() {
        voucherViewModel.observeVouchers.observe(viewLifecycleOwner) {
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

        voucherViewModel.observeCollectVoucher.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    // do nothing
                }
            }
        }
    }

    private fun updateUi(vouchers: List<Voucher>) {
        binding.emptyView.root.isVisible = vouchers.isEmpty()
        vouchersAdapter.submitList(vouchers)
    }
}