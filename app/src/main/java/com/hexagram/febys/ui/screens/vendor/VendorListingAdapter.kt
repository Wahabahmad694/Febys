package com.hexagram.febys.ui.screens.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.bindings.BindingAdapter
import com.hexagram.febys.databinding.ItemVendorStoreBinding
import com.hexagram.febys.databinding.ItemVendorStoreHeadingBinding
import com.hexagram.febys.models.view.VendorListing

class VendorListingAdapter(private val isCelebrity: Boolean) :
    PagingDataAdapter<VendorListing, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<VendorListing>() {

            override fun areItemsTheSame(oldItem: VendorListing, newItem: VendorListing): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: VendorListing, newItem: VendorListing
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_VENDOR = 2
    }

    var followVendor: ((vendor: Int) -> Unit)? = null
    var unFollowVendor: ((vendor: Int) -> Unit)? = null
    var gotoCelebrityDetail: ((vendor: Int) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is VendorListing.VendorListingHeader -> {
                (holder as HeaderViewHolder).bind(item)
            }
            is VendorListing.Vendor -> {
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
            is VendorListing.VendorListingHeader -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_VENDOR
        }
    }

    inner class VendorViewHolder(
        private val binding: ItemVendorStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VendorListing.Vendor, position: Int) {
            binding.apply {
                isCelebrity = this@VendorListingAdapter.isCelebrity

                root.setOnClickListener {
                    gotoDetailPage(item.id)
                }

                vendorName.text = item.name
                vendorType.text = item.role
                preferredVendorOrShopName.text = item.preferredVendorOrShopName
                storeRatingBar.max = 100
                storeRatingBar.progress = item.vendorRating

                val imageUrl =
                    if (this@VendorListingAdapter.isCelebrity) item.profileImage else item.businessLogo
                BindingAdapter.imageUrl(vendorImg, imageUrl)

                showFollowBtn = item is VendorListing.FollowingVendor
                isFollowing = (item as? VendorListing.FollowingVendor)?.isFollow ?: false

                btnToggleFollow.setOnClickListener {
                    if (isFollowing == null) return@setOnClickListener

                    isFollowing = !isFollowing!!
                    (item as? VendorListing.FollowingVendor)?.isFollow = isFollowing!!
                    notifyItemChanged(position)

                    if (isFollowing!!)
                        followVendor?.invoke(item.id) else unFollowVendor?.invoke(item.id)
                }
            }
        }

        private fun gotoDetailPage(id: Int) {
            if (isCelebrity) {
                gotoCelebrityDetail?.invoke(id)
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemVendorStoreHeadingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VendorListing.VendorListingHeader) {
            binding.apply {
                tvHeading.text = root.context.getString(item.title)
            }
        }
    }

}