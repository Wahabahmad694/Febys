package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.hexagram.febys.R
import com.hexagram.febys.databinding.LayoutRatingsBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val _id: String?,
    val score: Double,
    val count: Int
) : Parcelable {

    companion object {
        fun addRatingToUi(rating: Rating, scores: List<Rating>, binding: LayoutRatingsBinding) {
            binding.apply {
                averageRating.text = root.context
                    .getString(R.string.average_rating_based_on, rating.score, rating.count)

                fun updateRatingRow(
                    progressBar: LinearProgressIndicator, total: TextView, count: Int?
                ) {
                    val avg = count?.times(100.0)?.div(rating.count)?.toInt() ?: 0
                    progressBar.progress = avg
                    total.text = (count ?: 0).toString()
                }

                val star1Count = scores.firstOrNull { it.score.toInt() == 1 }?.count
                updateRatingRow(start1RatingBar, star1Total, star1Count)

                val star2Count = scores.firstOrNull { it.score.toInt() == 2 }?.count
                updateRatingRow(start2RatingBar, star2Total, star2Count)

                val star3Count = scores.firstOrNull { it.score.toInt() == 3 }?.count
                updateRatingRow(start3RatingBar, star3Total, star3Count)

                val star4Count = scores.firstOrNull { it.score.toInt() == 4 }?.count
                updateRatingRow(start4RatingBar, star4Total, star4Count)

                val star5Count = scores.firstOrNull { it.score.toInt() == 5 }?.count
                updateRatingRow(start5RatingBar, star5Total, star5Count)
            }
        }
    }
}
