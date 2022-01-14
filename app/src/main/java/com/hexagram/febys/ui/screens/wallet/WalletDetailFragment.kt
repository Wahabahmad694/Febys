package com.hexagram.febys.ui.screens.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentWalletDetailBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.showWarningDialog

class WalletDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentWalletDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListeners()

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