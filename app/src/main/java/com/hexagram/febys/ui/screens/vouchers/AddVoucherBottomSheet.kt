package com.hexagram.febys.ui.screens.vouchers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseBottomSheet
import com.hexagram.febys.databinding.BottomSheetAddVoucherBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.showToast

class AddVoucherBottomSheet : BaseBottomSheet() {
    companion object {
        const val REQ_KEY_ADD_VOUCHER = "reqKeyAddVoucher"
    }

    private lateinit var binding: BottomSheetAddVoucherBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetAddVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
    }

    private fun uiListener() {
        binding.btnClose.setOnClickListener { goBack() }

        binding.btnAdd.setOnClickListener {
            val voucher = binding.etVoucher.text.toString()
            if (voucher.isEmpty()) {
                showToast(getString(R.string.toast_enter_voucher_code))
                return@setOnClickListener
            }
            setFragmentResult(
                REQ_KEY_ADD_VOUCHER, bundleOf(REQ_KEY_ADD_VOUCHER to voucher)
            )
            goBack()
        }
    }

    override fun fullScreen() = false
}
