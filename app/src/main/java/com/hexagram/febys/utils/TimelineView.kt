package com.hexagram.febys.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemViewTimelineBinding
import com.hexagram.febys.databinding.ItemViewTimelineTitleBinding
import com.hexagram.febys.databinding.ViewTimelineBinding

class TimelineView : LinearLayout {
    val binding =
        ViewTimelineBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    fun submitTimeline(timelines: List<TimelineModel>) {
        drawTimeline(timelines)
    }

    private fun drawTimeline(timelines: List<TimelineModel>) {
        binding.containerTimelineView.removeAllViews()
        binding.containerTimelineTitle.removeAllViews()

        val lastCompletedIndex = timelines.indexOfLast { it.isCompleted }

        for ((index, timeline) in timelines.withIndex()) {
            if (index != 0) {
                val bgColorCode = when {
                    timeline.isError -> "#E04C38"
                    lastCompletedIndex == index - 1 || timeline.isCompleted -> "#6CE038"
                    else -> "#F7F7F7"
                }

                val separatorHeight = context.resources.getDimension(R.dimen._3sdp).toInt()
                val bgColor = Color.parseColor(bgColorCode)
                val timeLineSeparator = View(context).also {
                    it.layoutParams = LayoutParams(0, separatorHeight, 1F)
                    it.setBackgroundColor(bgColor)
                }
                binding.containerTimelineView.addView(timeLineSeparator)

                val timeLineTitleSeparator = View(context).also {
                    it.layoutParams = LayoutParams(0, separatorHeight, 1F)
                }
                binding.containerTimelineTitle.addView(timeLineTitleSeparator)
            }

            val imageRes = when {
                timeline.isError -> R.drawable.ic_timeline_error
                timeline.isCompleted -> R.drawable.ic_timeline_filled
                else -> R.drawable.ic_timeline
            }

            val timelineBinding = ItemViewTimelineBinding.inflate(
                LayoutInflater.from(context), binding.containerTimelineView, false
            )
            timelineBinding.ivTimeline.setImageResource(imageRes)
            binding.containerTimelineView.addView(timelineBinding.root)

            val textColorCode =
                if (timeline.isCompleted) "#353535" else "#979797"
            val textColor = Color.parseColor(textColorCode)
            val timelineTitleBinding = ItemViewTimelineTitleBinding.inflate(
                LayoutInflater.from(context), binding.containerTimelineTitle, false
            )
            timelineTitleBinding.tvTitleTimelineView.text = timeline.title
            timelineTitleBinding.tvTitleTimelineView.setTextColor(textColor)
            binding.containerTimelineTitle.addView(timelineTitleBinding.root)
        }
    }
}

data class TimelineModel(
    val title: String,
    val isCompleted: Boolean,
    val isError: Boolean
)