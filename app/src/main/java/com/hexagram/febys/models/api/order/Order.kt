package com.hexagram.febys.models.api.order

import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.R
import com.hexagram.febys.databinding.LayoutOrderSummaryBinding
import com.hexagram.febys.databinding.LayoutOrderSummaryProductBinding
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.request.EstimateRequest
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.vouchers.VoucherDetail
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.models.swoove.SwooveEstimates
import com.hexagram.febys.network.domain.util.CartMapper
import com.hexagram.febys.utils.convertTwoDecimal
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    @SerializedName("_id")
    val id: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("consumer_id")
    val consumerId: String,
    val consumer: Consumer,
    @SerializedName("shipping_detail")
    val shippingAddress: ShippingAddress?,
    @SerializedName("swoove_estimates")
    val swooveEstimates: SwooveEstimates?,
    @SerializedName("products_amount")
    val productsAmount: Price,
    @SerializedName("bill_amount")
    val billAmount: Price,
    @SerializedName("vendor_products")
    val vendorProducts: MutableList<VendorProducts>,
    @SerializedName("vat_percentage")
    val vatPercentage: Double,
    @SerializedName("delivery_fee")
    val deliveryFee: Price?,
    val transactions: List<Transaction>,
    val voucher: VoucherDetail?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    var totalAmountAsString: Price,
    val swoove: EstimateRequest?
) : Parcelable {
    fun toListOfCartDTO(): List<CartDTO> {
        return CartMapper().mapFromVendorProducts(vendorProducts)
    }

    fun addToOrderSummary(containerOrderSummary: LayoutOrderSummaryBinding) {
        containerOrderSummary.containerOrderSummaryProducts.removeAllViews()
        val context = containerOrderSummary.root.context

        val cartItems = toListOfCartDTO()

        var totalItems = 0
        cartItems.forEach {
            totalItems += it.quantity
            addProductToOrderSummary(containerOrderSummary, it.productName, it.quantity, it.price)
        }
        updateOrderSummaryQuantity(containerOrderSummary, totalItems)

//        addVatToOrderSummary(containerOrderSummary, vatPercentage, productsAmount)

        addProductToOrderSummary(
            containerOrderSummary,
            context.getString(R.string.label_subtotal),
            1,
            productsAmount,
            true
        )

        val shippingFee =
            if (swoove != null) listOf(swoove.estimate) else swooveEstimates?.responses?.estimates?.filter { it.selected }
        var shippingFinalFee =
            (shippingFee?.map { it.totalPricing.value }?.first { it > 0 })?.toDouble() ?: 0.0
        val deliveryFee = Price("", shippingFinalFee, productsAmount.currency)
        addProductToOrderSummary(
            containerOrderSummary,
            context.getString(R.string.label_shipping_fee),
            1,
            deliveryFee,
            true
        )


        val transactionFees = transactions.filter { it.transactionFee != null }
        var finalFee = 0f

        if (transactionFees.isNotEmpty()) {
            finalFee =
                transactionFees.map { it.transactionFee }.firstOrNull { it != null && it > 0f }
                    ?: 0f
            val transactionsPrice = Price("", finalFee.toDouble(), productsAmount.currency)
            addProductToOrderSummary(
                containerOrderSummary,
                context.getString(R.string.label_processing_fee),
                1,
                transactionsPrice,
                true
            )
        }

        if (voucher != null) {
            val voucherDiscount = voucher.discount ?: 0.0
            val voucherPrice = Price("", -voucherDiscount, productsAmount.currency)
            addProductToOrderSummary(
                containerOrderSummary,
                context.getString(R.string.label_voucher_discount),
                1,
                voucherPrice,
                true
            )
        }

        updateTotalAmount(containerOrderSummary, billAmount, finalFee, shippingFinalFee)
    }

    private fun addProductToOrderSummary(
        containerOrderSummary: LayoutOrderSummaryBinding,
        productName: String,
        quantity: Int,
        price: Price,
        hideQuantity: Boolean = false,
    ) {
        val context = containerOrderSummary.root.context
        val productSummary = LayoutOrderSummaryProductBinding.inflate(
            LayoutInflater.from(context), containerOrderSummary.containerOrderSummaryProducts, false
        )

        val productNameWithQuantity = if (hideQuantity) productName else "$quantity x $productName"
        productSummary.tvProductNameWithQuantity.text = productNameWithQuantity

        productSummary.tvTotalPrice.text = price.getFormattedPrice(quantity)
        containerOrderSummary.containerOrderSummaryProducts.addView(productSummary.root)
    }

//    private fun addVatToOrderSummary(
//        containerOrderSummary: LayoutOrderSummaryBinding,
//        vatPercentage: Double,
//        total: Price
//    ) {
//        val context = containerOrderSummary.root.context
//        val productSummary = LayoutOrderSummaryProductBinding.inflate(
//            LayoutInflater.from(context), containerOrderSummary.containerOrderSummaryProducts, false
//        )
//
//        val vatLabel = context.getString(R.string.label_vat)
//        val vatWithPercentage = "$vatLabel ($vatPercentage%)"
//        productSummary.tvProductNameWithQuantity.text = vatWithPercentage
//
//        val vatAmount =
//            if (vatPercentage > 0) total.value.times(vatPercentage).div(100.0) else 0.0
//        val vatPrice = Price("", vatAmount, total.currency)
//
//        productSummary.tvTotalPrice.text = vatPrice.getFormattedPrice()
//        containerOrderSummary.containerOrderSummaryProducts.addView(productSummary.root)
//    }

    private fun updateOrderSummaryQuantity(
        containerOrderSummary: LayoutOrderSummaryBinding,
        quantity: Int
    ) {
        val context = containerOrderSummary.root.context
        val itemString =
            if (quantity > 1) context.getString(R.string.label_items) else context.getString(R.string.label_item)
        val summaryQuantityString =
            context.getString(R.string.label_order_summary_with_quantity) + " ($quantity " + itemString + ")"
        containerOrderSummary.labelOrderSummary.text = summaryQuantityString
    }


    private fun updateTotalAmount(
        containerOrderSummary: LayoutOrderSummaryBinding,
        price: Price,
        transactions: Float?,
        shippingFinalFee: Double?
    ) {
        val total: Double = price.value.plus(transactions?.toDouble() ?: 0.0)
        val totalWithShippingFee = total.plus(shippingFinalFee ?: 0.0)
        Log.d("Price1", "updateTotal1: $totalWithShippingFee")
        if (swoove?.estimate == null) {
            totalAmountAsString =
                Price("", totalWithShippingFee, price.currency)
            containerOrderSummary.tvTotalPrice.text =
                "${totalAmountAsString.currency}${totalAmountAsString.value.toString().convertTwoDecimal()}"
        } else {
            val netPrice =( totalWithShippingFee.minus(swoove.estimate.totalPricing.value))
            Log.d("Price2", "updateTotal2: $netPrice")
            totalAmountAsString =
                Price("", netPrice, price.currency)
            containerOrderSummary.tvTotalPrice.text =
                "${totalAmountAsString.currency}${totalAmountAsString.value.toString().convertTwoDecimal()}"
        }

    }

}
