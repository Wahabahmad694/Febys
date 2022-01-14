package com.hexagram.febys.ui.screens.payment.method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentPaymentMethodsBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodsFragment : BaseFragment() {
    private lateinit var binding: FragmentPaymentMethodsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.labelWalletPayment.setOnClickListener {
            changeBackground(binding.labelWalletPayment, binding.walletFilledTick)
        }

        binding.ivBack.setOnClickListener { goBack() }

        binding.labelMomoPayment.setOnClickListener {
            changeBackground(binding.labelMomoPayment, binding.momoFilledTick)
        }

        binding.btnCheckout.setOnClickListener {
            goSuccessScreen()
        }

        binding.labelPaypalPayment.setOnClickListener {
            changeBackground(binding.labelPaypalPayment, binding.paypalFilledTick)
        }
    }

    private fun goSuccessScreen() {
        val gotoSuccessCheckout =
            PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToCheckoutSuccessFragment("FE-300")
        navigateTo(gotoSuccessCheckout)
    }

    private fun changeBackground(constraintLayout: ConstraintLayout, imageView: ImageView) {

        binding.walletFilledTick.visibility = View.GONE
        binding.momoFilledTick.visibility = View.GONE
        binding.paypalFilledTick.visibility = View.GONE

        binding.labelWalletPayment.background =
            activity?.resources?.getDrawable(R.drawable.bg_border_grey)
        binding.labelMomoPayment.background =
            activity?.resources?.getDrawable(R.drawable.bg_border_grey)
        binding.labelPaypalPayment.background =
            activity?.resources?.getDrawable(R.drawable.bg_border_grey)

        constraintLayout.background = activity?.resources?.getDrawable(R.drawable.bg_border_red)
        imageView.visibility = View.VISIBLE

    }

}

