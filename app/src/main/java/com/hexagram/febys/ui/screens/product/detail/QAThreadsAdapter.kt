package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemQuestionAnswersThreadBinding
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.utils.setDrawableRes

class QAThreadsAdapter : RecyclerView.Adapter<QAThreadsAdapter.QAThreadsVH>() {
    private var answers = mutableListOf<QAThread>()

    // consumerId must be set before calling submitList
    var consumerId: String = ""

    var replyTo: ((thread: QAThread) -> Unit)? = null
    var upVote: ((thread: QAThread, isRevoke: Boolean) -> Unit)? = null
    var downVote: ((thread: QAThread, isRevoke: Boolean) -> Unit)? = null

    inner class QAThreadsVH(
        private val binding: ItemQuestionAnswersThreadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QAThread, position: Int) = binding.apply {
            val question = item.chat.first()
            this.question.text = question.message
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

            val answers =
                if (item.chat.size > 1)
                    item.chat.subList(1, item.chat.size)
                else
                    mutableListOf()

            hasAnswers = answers.isNotEmpty()

            rvAnswers.adapter = AnswersAdapter().also { it.submitList(answers) }

            reply.setOnClickListener {
                replyTo?.invoke(item)
            }

            voteUp.setOnClickListener {
                upVote?.invoke(item, item.upVotes.contains(consumerId))
            }
            voteDown.setOnClickListener {
                downVote?.invoke(item, item.downVotes.contains(consumerId))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAThreadsVH {
        return QAThreadsVH(
            ItemQuestionAnswersThreadBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: QAThreadsVH, position: Int) {
        holder.bind(answers[position], position)
    }

    fun submitList(list: MutableList<QAThread>) {
        answers = list
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemCount(): Int {
        return answers.size
    }
}