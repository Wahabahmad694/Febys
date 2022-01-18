package com.hexagram.febys.ui.screens.order.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentAddEditReviewBinding
import com.hexagram.febys.models.api.cart.CartProduct
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.api.rating.OrderReview
import com.hexagram.febys.models.api.rating.ProductReview
import com.hexagram.febys.models.api.rating.Review
import com.hexagram.febys.models.api.rating.VendorReview
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditReviewFragment : BaseFragment() {
    companion object {
        const val REQ_KEY_REFRESH = "reqKeyRefresh"
    }

    private lateinit var binding: FragmentAddEditReviewBinding
    private val orderViewModel by viewModels<OrderViewModel>()
    private val args by navArgs<AddEditReviewFragmentArgs>()

    private val addEditReviewAdapter = AddEditProductReviewAdapter()
    private lateinit var orderReview: OrderReview

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        val vendor = args.vendorProducts.vendor

        orderReview = createAddEditReviewRequest(args.vendorProducts)
        updateUi(orderReview)

        binding.vendorName.text = vendor.shopName
        binding.vendorType.text = vendor.role.name
        binding.vendorImg.load(vendor.businessInfo.logo)

        binding.rvAddEditProductReviews.isNestedScrollingEnabled = false
        binding.rvAddEditProductReviews.adapter = addEditReviewAdapter
        binding.rvAddEditProductReviews.applySpaceItemDecoration(R.dimen._16sdp)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }
        binding.ivEdit.setOnClickListener {
            updateFieldAndReviewsAdapter(true)

        }
        binding.etComment.doAfterTextChanged {
            orderReview.vendorReview.review.comment = it.toString()
        }

        binding.priceRating.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                if (rating < 1f) binding.priceRating.ratingBar.progress = 1
                orderReview.vendorReview.pricingScore = rating.toInt().toDouble()
            }
        }
        binding.qualityRating.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                if (rating < 1f) binding.qualityRating.ratingBar.progress = 1
                orderReview.vendorReview.qualityScore = rating.toInt().toDouble()
            }
        }
        binding.valueRating.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                if (rating < 1f) binding.valueRating.ratingBar.progress = 1
                orderReview.vendorReview.valueScore = rating.toInt().toDouble()
            }
        }

        addEditReviewAdapter.ratingCallback = { skuId, rating ->
            orderReview.productsRatings.firstOrNull { it.skuId == skuId }?.score = rating.toDouble()
        }

        addEditReviewAdapter.commentCallback = { skuId, comment ->
            orderReview.productsRatings
                .firstOrNull { it.skuId == skuId }?.review?.comment = comment
        }

        binding.btnSubmitReview.setOnClickListener {
            orderViewModel.postReview(args.orderId, orderReview)
        }
    }


    private fun setObserver() {
        orderViewModel.observeOrderReview.observe(viewLifecycleOwner) {
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
                    showToast("Review Posted")
                    updateFieldAndReviewsAdapter(false)
                    setFragmentResult(
                        REQ_KEY_REFRESH,
                        bundleOf(REQ_KEY_REFRESH to true)
                    )
                }
            }
        }
    }

    private fun updateUi(orderReview: OrderReview) {
        val hasReviewed = args.vendorProducts.hasReviewed
        updateFieldAndReviewsAdapter(isEnable = !hasReviewed)

        binding.etComment.setText(orderReview.vendorReview.review.comment)
        binding.priceRating.ratingBar.progress = orderReview.vendorReview.pricingScore.toInt()
        binding.qualityRating.ratingBar.progress = orderReview.vendorReview.qualityScore.toInt()
        binding.valueRating.ratingBar.progress = orderReview.vendorReview.valueScore.toInt()
    }

    private fun addReviewsToProduct(products: MutableList<CartProduct>): MutableList<CartProduct> {
        products.forEach { cartProduct ->
            val productReview = orderReview.productsRatings
                .firstOrNull { it.skuId == cartProduct.product.variants[0].skuId }

            cartProduct.ratingAndReview = ProductReview(
                orderId = productReview?.orderId ?: args.orderId,
                productId = productReview?.productId ?: cartProduct.product._id,
                skuId = productReview?.skuId ?: cartProduct.product.variants[0].skuId,
                score = productReview?.score ?: 5.0,
                review = productReview?.review ?: Review("")
            )
        }

        return products
    }

    private fun updateFieldAndReviewsAdapter(isEnable: Boolean) {
        binding.ivEdit.isVisible = !isEnable
        if (isEnable) {
            binding.labelAddReview.setText(getText(R.string.label_edit_review))
        }
        binding.btnSubmitReview.isVisible = isEnable
        binding.priceRating.ratingBar.setIsIndicator(!isEnable)
        binding.qualityRating.ratingBar.setIsIndicator(!isEnable)
        binding.valueRating.ratingBar.setIsIndicator(!isEnable)
        binding.etComment.isEnabled = isEnable

        val productsWithReviews = addReviewsToProduct(args.vendorProducts.products)
        addEditReviewAdapter.submitList(productsWithReviews, isEnable)
    }

    private fun createAddEditReviewRequest(vendorProducts: VendorProducts): OrderReview {
        val listOfProductReviews = mutableListOf<ProductReview>()
        vendorProducts.products.forEach {
            val productReview = ProductReview(
                _id = it.ratingAndReview?._id,
                consumerId = it.ratingAndReview?.consumerId,
                orderId = it.ratingAndReview?.orderId ?: args.orderId,
                productId = it.ratingAndReview?.productId ?: it.product._id,
                skuId = it.ratingAndReview?.skuId ?: it.product.variants[0].skuId,
                upVotes = it.ratingAndReview?.upVotes,
                downVotes = it.ratingAndReview?.downVotes,
                review = it.ratingAndReview?.review ?: Review(""),
                score = it.ratingAndReview?.score ?: 5.0,
                createdAt = it.ratingAndReview?.createdAt,
                updatedAt = it.ratingAndReview?.updatedAt
            )
            listOfProductReviews.add(productReview)
        }

        val vendorRating = VendorReview(
            _id = vendorProducts.ratingAndReview?._id,
            consumerId = vendorProducts.ratingAndReview?.consumerId,
            consumer = vendorProducts.ratingAndReview?.consumer,
            orderId = vendorProducts.ratingAndReview?.orderId ?: args.orderId,
            vendorId = vendorProducts.ratingAndReview?.vendorId ?: vendorProducts.vendor._id,
            valueScore = vendorProducts.ratingAndReview?.valueScore ?: 5.0,
            pricingScore = vendorProducts.ratingAndReview?.pricingScore ?: 5.0,
            qualityScore = vendorProducts.ratingAndReview?.qualityScore ?: 5.0,
            review = vendorProducts.ratingAndReview?.review ?: Review(""),
            createdAt = vendorProducts.ratingAndReview?.createdAt,
            updatedAt = vendorProducts.ratingAndReview?.updatedAt
        )

        return OrderReview(
            listOfProductReviews, vendorRating
        )
    }
}