package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCheckoutBinding
import com.hexagram.febys.databinding.LayoutOrderSummaryProductBinding
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.ui.screens.cart.CartAdapter
import com.hexagram.febys.ui.screens.dialog.InfoDialog
import com.hexagram.febys.utils.*
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

        binding.containerPayment.setOnClickListener {
            val gotoPaymentMethod =
                CheckoutFragmentDirections.actionCheckoutFragmentToPaymentMethodsFragment()
            navigateTo(gotoPaymentMethod)
        }

        binding.btnPlaceOrder.setOnClickListener {
            doPayment()
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
            createOrderSummary(sortedListForCart)

            if (sortedListForCart.isNullOrEmpty()) goBack()
        }
    }

    private fun createOrderSummary(cart: List<CartDTO>?) {
        binding.containerOrderSummary.containerOrderSummaryProducts.removeAllViews()

        updateOrderSummaryQuantity(cart?.size ?: 0)

        cart?.forEach {
            val price = if (it.hasVariantPromotion)
                it.promotionPrice?.toDouble() ?: 0.0
            else
                it.variantPrice

            addProductToOrderSummary(it.productName, it.quantity, price)
        }

        val subtotal: Double = cart?.sumOf {
            (if (it.hasVariantPromotion) it.promotionPrice?.toDouble() ?: 0.0 else it.variantPrice)
                .times(it.quantity)
        } ?: 0.0

        addProductToOrderSummary(getString(R.string.label_subtotal), 1, subtotal)
        addProductToOrderSummary(getString(R.string.label_vat), 1, 10.0)
        addProductToOrderSummary(getString(R.string.label_shipping_fee), 1, 100.0)
        calculateAndUpdateTotalAmount(subtotal)
    }

    private fun addProductToOrderSummary(productName: String, quantity: Int, price: Double) {
        val productSummary = LayoutOrderSummaryProductBinding.inflate(
            layoutInflater,
            binding.containerOrderSummary.containerOrderSummaryProducts,
            false
        )

        val productNameWithQuantity = if (quantity > 1) "$productName x $quantity" else productName
        productSummary.tvProductNameWithQuantity.text = productNameWithQuantity

        val total = price.times(quantity).toFixedDecimal(2)
        productSummary.tvTotalPrice.text = getString(R.string.price_with_dollar_sign, total)
        binding.containerOrderSummary.containerOrderSummaryProducts.addView(productSummary.root)
    }

    private fun updateOrderSummaryQuantity(quantity: Int) {
        binding.containerOrderSummary.labelOrderSummary.text =
            getString(R.string.label_order_summary_with_quantity, quantity)
    }

    private fun calculateAndUpdateTotalAmount(
        subtotal: Double, vat: Double = 10.0, shippingCost: Double = 100.0
    ) {
        val totalPrice = subtotal.plus(shippingCost).plus(vat)
        val totalAmountAsString =
            getString(R.string.price_with_dollar_sign, totalPrice.toFixedDecimal(2))

        binding.tvTotalAmount.text = totalAmountAsString
        binding.containerOrderSummary.tvTotalPrice.text = totalAmountAsString

    }

    private fun updateShippingAddressUi(shippingAddress: ShippingAddress?) {
        binding.tvShippingAddress.text =
            shippingAddress?.fullAddress() ?: getString(R.string.msg_for_no_shipping_address)
    }

    private fun updatePaymentMethod(paymentMethod: PaymentMethod?) {
        if (paymentMethod != null) {
            binding.tvPayment.text = paymentMethod.name
            binding.icPayment.show()
            binding.icPayment.setImageResource(paymentMethod.img)
        } else {
            binding.tvPayment.text = getString(R.string.msg_for_no_payment)
            binding.icPayment.invisible()
        }
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

    private fun doPayment() {
        checkoutViewModel.clearCart()
        gotoCheckoutSuccessScreen("FT133659380093")
    }

    private fun gotoCheckoutSuccessScreen(orderId: String) {
        val destination = CheckoutFragmentDirections
            .actionCheckoutFragmentToCheckoutSuccessFragment(orderId)
        navigateTo(destination)
    }
}