package com.hexagram.febys.models.api.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.vouchers.VoucherDetail
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.domain.util.CartMapper
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
    val updatedAt: String
) : Parcelable {
    fun toListOfCartDTO(): List<CartDTO> {
        return CartMapper().mapFromVendorProducts(vendorProducts)
    }
}
