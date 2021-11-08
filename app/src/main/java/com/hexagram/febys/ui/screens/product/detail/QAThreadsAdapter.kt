package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemQuestionAnswersThreadBinding
import com.hexagram.febys.models.api.product.QAThread

class QAThreadsAdapter : RecyclerView.Adapter<QAThreadsAdapter.QAThreadsVH>() {
    private var answers = mutableListOf<QAThread>()

    // consumerId must be set before calling submitList
    var consumerId: String = ""

    inner class QAThreadsVH(
        private val binding: ItemQuestionAnswersThreadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QAThread, position: Int) = binding.apply {
            val question = item.chat.first()
            this.question.text = question.message
            voteUp.text = item.upVotes.size.toString()
            voteDown.text = item.downVotes.size.toString()

            // todo update voteUp and voteDown icon

            val answers =
                if (item.chat.size > 1)
                    item.chat.subList(1, item.chat.size)
                else
                    mutableListOf()

            hasAnswers = answers.isNotEmpty()

            rvAnswers.adapter = AnswersAdapter().also { it.submitList(answers) }
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