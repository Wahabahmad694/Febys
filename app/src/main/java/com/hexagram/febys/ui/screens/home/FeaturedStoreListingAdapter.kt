package com.hexagram.febys.ui.screens.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.bindings.BindingAdapter
import com.hexagram.febys.databinding.ItemStoreFeaturedBinding
import com.hexagram.febys.models.api.vendor.Vendor

class FeaturedStoreListingAdapter :
    ListAdapter<Vendor, FeaturedStoreListingAdapter.FeaturedStoreListingViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Vendor>() {

            override fun areItemsTheSame(
                oldItem: Vendor,
                newItem: Vendor
            ): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(
                oldItem: Vendor,
                newItem: Vendor
            ): Boolean {
                return oldItem._id == newItem._id
            }
        }
    }

    var interaction: Interaction? = null

    var gotoVendorDetail: ((vendorId: String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedStoreListingAdapter.FeaturedStoreListingViewHolder {
        return FeaturedStoreListingViewHolder(
            ItemStoreFeaturedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FeaturedStoreListingAdapter.FeaturedStoreListingViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), position)
    }

    inner class FeaturedStoreListingViewHolder(val binding: ItemStoreFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item:Vendor, position: Int) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(position, item)
                gotoVendorDetail?.invoke(item._id)
            }
            val imageUrl = item.businessInfo.logo
            BindingAdapter.imageUrl(binding.storeImg, imageUrl)

            binding.tvStoreName.text = item.name
            if (item.official) {
                binding.ivBadge.isVisible = true
            }
            val storeRating = item.stats.rating.score
            binding.storeRatingBar.rating = storeRating.toFloat()
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Vendor)
    }
}