package com.hexagram.febys.ui.screens.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCartBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
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
        binding.rvCart.isNestedScrollingEnabled = false
        binding.rvCart.adapter = cartAdapter

        updateFav()
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            goBack()
        }

        binding.btnProceedToCheckout.setOnClickListener {
            gotoCheckout()
        }

        cartAdapter.interaction = object : CartAdapter.Interaction {
            override fun updateCartItem(cartDTO: CartDTO) {
                cartViewModel.updateCartItem(cartDTO)
            }

            override fun toggleFavIfUserLoggedIn(skuId: String): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        cartViewModel.toggleFav(skuId)
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
                    NavGraphDirections.actionToProductDetail(cartDTO.productId, cartDTO.skuId)
                navigateTo(navigateToProductDetail)
            }
        }
    }

    private fun gotoCheckout() {
        val gotoCheckout = CartFragmentDirections.actionCartFragmentToCheckoutFragment()
        navigateTo(gotoCheckout)
    }

    private fun updateFav() {
        val fav = cartViewModel.getFav()
        cartAdapter.submitFav(fav)
    }

    private fun setupObserver() {
        cartViewModel.observeCart().observe(viewLifecycleOwner) {
           updateUi(it)
        }
    }

    private fun updateGotoCheckoutBtnVisibility(isVisible: Boolean) {
        binding.btnProceedToCheckout.isVisible = isVisible
    }

    private fun updateUi(cart: List<CartDTO>?) {
        val sortedListForCart = cartViewModel.sortListForCart(cart)

        cartAdapter.submitList(sortedListForCart)

        updateGotoCheckoutBtnVisibility(!sortedListForCart.isNullOrEmpty())

        val itemsTotal: Double = sortedListForCart?.sumOf { it.price.value.times(it.quantity) } ?: 0.0

        binding.tvSubtotalAmount.text =
            getString(R.string.variant_price, itemsTotal.toFixedDecimal(2))

        binding.tvTotalAmount.text =
            getString(R.string.variant_price, itemsTotal.toFixedDecimal(2))
    }

    override fun getTvCartCount() = binding.tvCartCount
}