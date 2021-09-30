package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCheckoutBinding
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.models.view.CheckoutModel
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.ui.screens.cart.CartAdapter
import com.hexagram.febys.ui.screens.dialog.InfoDialog
import com.hexagram.febys.ui.screens.shipping.address.ShippingAddressFragment
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val cartAdapter = CartAdapter(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvCart.isNestedScrollingEnabled = false
        binding.rvCart.adapter = cartAdapter

        val shippingAddress = checkoutViewModel.getDefaultShippingAddress()
        updateShippingAddressUi(shippingAddress)

        val paymentMethod = checkoutViewModel.getDefaultPaymentMethod()
        updatePaymentMethod(paymentMethod)

        updateFav()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.containerShippingAddress.setOnClickListener {
            showChangeShippingAddressWarningDialog()
        }

        cartAdapter.interaction = object : CartAdapter.Interaction {
            override fun updateCartItem(cartDTO: CartDTO) {
                checkoutViewModel.updateCartItem(cartDTO)
            }

            override fun toggleFavIfUserLoggedIn(variantId: Int): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        checkoutViewModel.toggleFav(variantId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }

            override fun removeFromCart(cartDTO: CartDTO) {
                checkoutViewModel.removeFromCart(cartDTO)
            }

            override fun openProductDetail(cartDTO: CartDTO) {
                val navigateToProductDetail =
                    NavGraphDirections.actionToProductDetail(cartDTO.productId, cartDTO.variantId)
                navigateTo(navigateToProductDetail)
            }
        }
    }

    private fun setObserver() {
        checkoutViewModel.observeCart().observe(viewLifecycleOwner) {
            val sortedListForCart = checkoutViewModel.sortListForCart(it)
            cartAdapter.submitList(sortedListForCart)
            calculateAndUpdatePrices(sortedListForCart)
        }
    }

    private fun calculateAndUpdatePrices(cart: List<CartDTO>?, shippingCost: Double = 100.0) {
        val itemsTotal: Double = cart?.sumOf {
            (it.promotionPrice?.toDouble() ?: it.variantPrice).times(it.quantity)
        } ?: 0.0

//        binding.tvSubtotalAmount.text =
//            getString(R.string.variant_price, itemsTotal.toFixedDecimal(2))

//        binding.tvShippingAmount.text =
//            getString(R.string.variant_price, shippingCost.toFixedDecimal(2))

        val totalPrice = itemsTotal.plus(shippingCost)

//        binding.tvTotalAmount.text =
//            getString(R.string.variant_price, totalPrice.toFixedDecimal(2))
    }

    private fun updateUi(checkoutModel: CheckoutModel) {
        updateShippingAddressUi(checkoutModel.shippingAddress)
        updatePaymentMethod(checkoutModel.paymentMethod)
    }

    private fun updateShippingAddressUi(shippingAddress: ShippingAddress?) {
        binding.tvShippingAddress.text =
            shippingAddress?.fullAddress() ?: getString(R.string.msg_for_no_shipping_address)
    }

    private fun updatePaymentMethod(paymentMethod: PaymentMethod?) {
        binding.tvPayment.text =
            paymentMethod?.name ?: getString(R.string.msg_for_no_payment)
    }

    private fun showChangeShippingAddressWarningDialog() {
        val resId = R.drawable.ic_shipping_address
        val title = getString(R.string.label_shipping_address)
        val msg = getString(R.string.msg_for_change_shipping_address)

        InfoDialog(resId, title, msg) { gotoShippingAddress() }
            .show(childFragmentManager, InfoDialog.TAG)
    }

    private fun updateFav() {
        val fav = checkoutViewModel.getFav()
        cartAdapter.submitFav(fav)
    }

    private fun gotoShippingAddress() {
        val gotoShippingAddress =
            CheckoutFragmentDirections.actionCheckoutFragmentToShippingAddressFragment()
        navigateTo(gotoShippingAddress)
    }
}