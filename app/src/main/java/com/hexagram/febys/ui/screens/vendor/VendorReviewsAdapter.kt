package com.hexagram.febys.ui.screens.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemVendorReviewBinding
import com.hexagram.febys.models.api.rating.VendorReview
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.load

class VendorReviewsAdapter : RecyclerView.Adapter<VendorReviewsAdapter.ReviewVH>() {
    private var ratingAndReviews = listOf<VendorReview>()

    inner class ReviewVH(
        private val binding: ItemVendorReviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VendorReview) = binding.apply {
            userImage.load(item.consumer?.profileImage)
            userName.text = item.consumer?.fullName
            ratingPrice.text = item.pricingScore.toInt().toString()
            ratingValue.text = item.valueScore.toInt().toString()
            ratingQuality.text = item.qualityScore.toInt().toString()
            tvReview.text = item.review.comment
            tvReview.isVisible = item.review.comment.isNotEmpty()
            date.text =
                Utils.DateTime.formatDate(
                    item.createdAt ?: "", Utils.DateTime.FORMAT_MONTH_DATE_YEAR
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        return ReviewVH(
            ItemVendorReviewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewVH, position: Int) {
        holder.bind(ratingAndReviews[position])
    }

    fun submitList(list: List<VendorReview>) {
        ratingAndReviews = list
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemCount(): Int {
        return ratingAndReviews.size
    }
}