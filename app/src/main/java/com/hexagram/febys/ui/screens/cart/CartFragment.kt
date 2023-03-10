package com.hexagram.febys.ui.screens.cart

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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
import okhttp3.ResponseBody


@AndroidEntryPoint
class CartFragment : BaseFragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModels()

    private val cartAdapter = CartAdapter()

    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            permissionCallback?.invoke(isGranted)
        }

    private var openDocumentTreeCallback: ((Uri) -> Unit)? = null
    private val openDocumentTree =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            if (it == null) return@registerForActivityResult
            val takeFlags: Int =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(it, takeFlags)
            openDocumentTreeCallback?.invoke(it)
        }


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
//        binding.tvShippingAmount.text = shippingPrice.getFormattedPrice()

        updateFav()
    }

    private fun uiListener() {
        binding.ivClose.setOnClickListener {
            goBack()
        }

        binding.btnDownloadPdf.setOnClickListener {
            val resId = R.drawable.ic_pdf
            val title = getString(R.string.label_delete_warning)
            val msg = getString(R.string.msg_for_download_pdf)

            showWarningDialog(resId, title, msg) {
                requestPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    cartViewModel.exportPdf()
                }
            }
        }

        binding.btnProceedToCheckout.setOnClickListener {
            if (isUserLoggedIn) gotoCheckout() else gotoLogin()
        }
        cartAdapter.gotoVendorDetail = { vendorID -> gotoVendorDetail(vendorID, false) }

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
            val response = it.getContentIfNotHandled()
            when (response) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(response).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    savePdf(response.data)
                }
            }
        }
    }

    private fun savePdf(body: ResponseBody) {
        askUserToSelectDirectory { selectedDirectory ->
            val pdfUri = FileUtils.writeResponseToFile(
                requireContext(), selectedDirectory, "application/pdf", "febys_cart.pdf", body
            )
            if (pdfUri != null) {
                showSuccessDialog(pdfUri)
            } else {
                showToast(getString(R.string.toast_fail_pdf))
            }
        }
    }

    private fun showSuccessDialog(pdfUri: Uri) {
        val resId = R.drawable.ic_pdf
        val title = getString(R.string.msg_congrats)
        val msg = getString(R.string.msg_for_pdf_success)

        showInfoDialoge(resId, title, msg) {
            Utils.openUri(requireContext(), pdfUri)
        }

    }

    private fun gotoVendorDetail(vendorId: String, isFollow: Boolean) {
        val direction = NavGraphDirections.toVendorDetailFragment(vendorId, isFollow)
        navigateTo(direction)
    }

    private fun askUserToSelectDirectory(callback: (Uri) -> Unit) {
        val contentResolver = requireContext().contentResolver
        val uri = contentResolver.persistedUriPermissions.firstOrNull()?.uri
        if (uri != null) {
            callback.invoke(uri)
            return
        }

        openDocumentTreeCallback = callback
        openDocumentTree.launch(null)
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

        binding.tvTotalAmount.text = formattedTotalPrice
    }


    private fun requestPermission(
        context: Context, permission: String, permissionCallback: (Boolean) -> Unit
    ) {
        this.permissionCallback = permissionCallback
        when {
            ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                this.permissionCallback?.invoke(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showRationalPermissionDialog(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }


    private fun showRationalPermissionDialog(permission: String) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.label_permission_required))
            .setMessage(getString(R.string.label_permission_msg))
            .setPositiveButton(R.string.okay) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton(R.string.label_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}