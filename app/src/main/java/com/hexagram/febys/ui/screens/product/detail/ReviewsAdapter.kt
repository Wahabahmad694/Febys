package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemReviewsBinding
import com.hexagram.febys.models.api.product.RatingAndReviews
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.setDrawableRes

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewVH>() {
    private var ratingAndReviews = listOf<RatingAndReviews>()

    // consumerId must be set before calling submitList
    var consumerId: String = ""

    var upVote: ((thread: RatingAndReviews, isRevoke: Boolean) -> Unit)? = null
    var downVote: ((thread: RatingAndReviews, isRevoke: Boolean) -> Unit)? = null

    inner class ReviewVH(
        private val binding: ItemReviewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RatingAndReviews, position: Int) = binding.apply {
            userName.text = item.consumer.firstName
            userRatingBar.progress = item.score.toInt()
            tvReview.text = item.review.comment
            date.text =
                Utils.DateTime.formatDate(item.createdAt, Utils.DateTime.FORMAT_MONTH_DATE_YEAR)

            tvReview.isVisible = item.review.comment.isNotEmpty()

            voteUp.text = item.upVotes.size.toString()
            voteDown.text = item.downVotes.size.toString()

            val voteUpDrawable = if (item.upVotes.contains(consumerId)) {
                R.drawable.ic_vote_up_fill
            } else {
                R.drawable.ic_vote_up
            }
            voteUp.setDrawableRes(voteUpDrawable)

            val voteDownDrawable = if (item.downVotes.contains(consumerId)) {
                R.drawable.ic_vote_down_fill
            } else {
                R.drawable.ic_vote_down
            }
            voteDown.setDrawableRes(voteDownDrawable)

            voteUp.setOnClickListener {
                upVote?.invoke(item, item.upVotes.contains(consumerId))
            }
            voteDown.setOnClickListener {
                downVote?.invoke(item, item.downVotes.contains(consumerId))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        return ReviewVH(
            ItemReviewsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewVH, position: Int) {
        holder.bind(ratingAndReviews[position], position)
    }

    fun submitList(list: List<RatingAndReviews>) {
        ratingAndReviews = list
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemCount(): Int {
        return ratingAndReviews.size
    }
}