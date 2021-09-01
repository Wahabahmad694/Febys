package com.hexagram.febys.ui.screens.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCartBinding
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo
import com.hexagram.febys.utils.toFixedDecimal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : BaseFragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val cartAdapter = CartAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel.refreshCart()

        initUi()
        uiListener()
        setupObserver()
    }

    private fun initUi() {
        binding.rvCart.setHasFixedSize(true)
        binding.rvCart.adapter = cartAdapter

        updateFav()
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            goBack()
        }

        cartAdapter.interaction = object : CartAdapter.Interaction {
            override fun updateCartItem(cartDTO: CartDTO) {
                cartViewModel.updateCartItem(cartDTO)
            }

            override fun toggleFavIfUserLoggedIn(variantId: Int): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        cartViewModel.toggleFav(variantId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }

            override fun removeFromCart(cartDTO: CartDTO) {
                cartViewModel.removeFromCart(cartDTO)
            }

            override fun openProductDetail(cartDTO: CartDTO) {
                val navigateToProductDetail =
                    NavGraphDirections.actionToProductDetail(cartDTO.productId, cartDTO.variantId)
                navigateTo(navigateToProductDetail)
            }
        }
    }

    private fun updateFav() {
        val fav = cartViewModel.getFav()
        cartAdapter.submitFav(fav)
    }

    private fun setupObserver() {
        cartViewModel.observeCart().observe(viewLifecycleOwner) {
            val sortedListForCart = cartViewModel.sortListForCart(it)
            cartAdapter.submitList(sortedListForCart)
            calculateAndUpdatePrices(sortedListForCart)
        }
    }

    private fun calculateAndUpdatePrices(cart: List<CartDTO>?) {
        val totalPrice: Double = cart?.sumOf {
            (it.promotionPrice?.toDouble() ?: it.variantPrice).times(it.quantity)
        } ?: 0.0

        binding.tvTotalAmount.text =
            getString(R.string.variant_price, totalPrice.toFixedDecimal(2))

        binding.tvSubtotalAmount.text =
            getString(R.string.variant_price, totalPrice.toFixedDecimal(2))

        binding.tvShippingAmount.text =
            getString(R.string.variant_price, "0.00")
    }

    override fun getTvCartCount() = binding.tvCartCount
}