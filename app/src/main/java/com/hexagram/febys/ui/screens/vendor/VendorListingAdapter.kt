package com.hexagram.febys.ui.screens.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.bindings.BindingAdapter
import com.hexagram.febys.databinding.ItemVendorStoreBinding
import com.hexagram.febys.databinding.ItemVendorStoreHeadingBinding
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.view.ListingHeader

class VendorListingAdapter(private val isCelebrity: Boolean) :
    PagingDataAdapter<Any, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Any>() {

            override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
                oldItem is Vendor && newItem is Vendor -> {
                    oldItem._id == newItem._id
                }
                oldItem is ListingHeader && newItem is ListingHeader -> {
                    oldItem.title == newItem.title
                }
                else -> {
                    false
                }
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any) = when {
                oldItem is Vendor && newItem is Vendor -> {
                    (oldItem as Vendor) == (newItem as Vendor)
                }
                oldItem is ListingHeader && newItem is ListingHeader -> {
                    (oldItem as ListingHeader) == (newItem as ListingHeader)
                }
                else -> {
                    false
                }
            }
        }

        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_VENDOR = 2
    }

    var followVendor: ((vendor: Vendor, position: Int) -> Unit)? = null
    var unFollowVendor: ((vendor: Vendor, position: Int) -> Unit)? = null
    var gotoCelebrityDetail: ((vendor: Vendor) -> Unit)? = null
    var gotoVendorDetail: ((vendor: Vendor) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListingHeader -> {
                (holder as HeaderViewHolder).bind(item)
            }
            is Vendor -> {
                (holder as VendorViewHolder).bind(item, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = when (viewType) {
            VIEW_TYPE_HEADER -> {
                HeaderViewHolder(
                    ItemVendorStoreHeadingBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                VendorViewHolder(
                    ItemVendorStoreBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListingHeader -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_VENDOR
        }
    }

    inner class VendorViewHolder(
        private val binding: ItemVendorStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Vendor, position: Int) {
            binding.apply {
                isCelebrity = this@VendorListingAdapter.isCelebrity

                root.setOnClickListener {
                    gotoDetailPage(item)
                }

                if (item.official) {
                    ivBadge.isVisible = true
                }

                vendorName.text = if (isCelebrity!!) item.name else item.shopName
                vendorType.text = item.role.name
                preferredVendorOrShopName.text = item.businessInfo.address

                val storeRating = item.stats.rating.score
                binding.storeRatingBar.rating = storeRating.toFloat()
                binding.storeRatingBar.stepSize = 0.5f

                val imageUrl = item.businessInfo.logo
                BindingAdapter.imageUrl(vendorImg, imageUrl)

                isFollowing = item.isFollow
                showFollowButton = item.showFollowButton

                btnToggleFollow.setOnClickListener {
                    if (isFollowing == null) return@setOnClickListener

                    if (isFollowing!!.not()) followVendor?.invoke(item, position)
                    else unFollowVendor?.invoke(item, position)
                }
            }
        }

        private fun gotoDetailPage(vendor: Vendor) {
            if (isCelebrity) {
                gotoCelebrityDetail?.invoke(vendor)
            } else {
                gotoVendorDetail?.invoke(vendor)
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemVendorStoreHeadingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListingHeader) {
            binding.apply {
                tvHeading.text = root.context.getString(item.title)
            }
        }
    }

}