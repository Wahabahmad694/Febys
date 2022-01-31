package com.hexagram.febys.ui.screens.cart

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hexagram.febys.BuildConfig
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCartBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : BaseFragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val downloadViewModel: AppUtils by viewModels()

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

        val shippingPrice = Price("", 0.0, Utils.DEFAULT_CURRENCY)
        binding.tvShippingAmount.text = shippingPrice.getFormattedPrice()

        updateFav()
    }

//    private fun downloadPdfObserver() {
//        downloadViewModel.observerDownloadPdf.observe(this, {
//            when (it) {
//                DownloadState.DOWNLOADING -> {
//
//                }
//                DownloadState.ERROR -> {
//                    hideDownloadProgress()
//                    showToast(getString(R.string.message_something_went_wrong_str))
//                }
//                DownloadState.COMPLETED -> {
//                    hideDownloadProgress()
//                }
//                DownloadState.NOT_STARTED -> {
//                    hideDownloadProgress()
//                }
//            }
//        })
//    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            goBack()
        }

        binding.btnDownloadPdf.setOnClickListener {
            val resId = R.drawable.ic_pdf
            val title = getString(R.string.label_delete_warning)
            val msg = getString(R.string.msg_for_download_pdf)

            showWarningDialog(resId, title, msg) {
//              cartViewModel.exportPdf()
                val uri = Uri.parse("${BuildConfig.backendBaseUrl}v1/cart/export/pdf")
                downloadViewModel.startMessageMediaDownload(uri.toString())


            }
        }

        binding.btnProceedToCheckout.setOnClickListener {
            if (isUserLoggedIn) gotoCheckout() else gotoLogin()
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
                val resId = R.drawable.ic_warning
                val title = getString(R.string.label_delete_warning)
                val msg = getString(R.string.msg_for_delete_item_bag)

                showWarningDialog(resId, title, msg) {
                    cartViewModel.removeFromCart(cartDTO)
                }
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

    private fun gotoLogin() {
        val gotoLogin = NavGraphDirections.actionToLoginFragment()
        navigateTo(gotoLogin)
    }

    private fun updateFav() {
        val fav = cartViewModel.getFav()
        cartAdapter.submitFav(fav)
    }

    private fun setupObserver() {
        cartViewModel.observeCart().observe(viewLifecycleOwner) {
            updateUi(it)
        }

        cartDataSource.observeCartCount().observe(viewLifecycleOwner) { cartCount ->
            binding.tvCartCount.text =
                if (cartCount == null || cartCount == 0) "" else "($cartCount)"
        }
        cartViewModel.observerDownloadPdf.observe(viewLifecycleOwner) {
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
                    it.data
                }
            }
        }
    }

    private fun updateBtnVisibilities(isVisible: Boolean) {
        binding.btnProceedToCheckout.isEnabled = isVisible
        binding.btnDownloadPdf.isVisible = isVisible
    }

    private fun updateUi(cart: List<CartDTO>?) {
        val sortedListForCart = cartViewModel.sortListForCart(cart)

        cartAdapter.submitList(sortedListForCart)

        updateBtnVisibilities(!sortedListForCart.isNullOrEmpty())

        val itemsTotal: Double =
            sortedListForCart?.sumOf { it.price.value.times(it.quantity) } ?: 0.0
        val priceCurrency = sortedListForCart?.firstOrNull()?.price?.currency ?: ""
        val formattedTotalPrice = if (priceCurrency.isEmpty())
            Price("", itemsTotal, Utils.DEFAULT_CURRENCY).getFormattedPrice()
        else
            Price("", itemsTotal, priceCurrency).getFormattedPrice()

        binding.tvSubtotalAmount.text = formattedTotalPrice
        binding.tvTotalAmount.text = formattedTotalPrice
    }
}