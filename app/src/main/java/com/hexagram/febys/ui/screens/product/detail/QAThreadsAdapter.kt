package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemQuestionAnswersThreadBinding
import com.hexagram.febys.models.view.QuestionAnswersThread

class QAThreadsAdapter : RecyclerView.Adapter<QAThreadsAdapter.QAThreadsVH>() {
    private var answers = mutableListOf<QuestionAnswersThread>()

    inner class QAThreadsVH(
        private val binding: ItemQuestionAnswersThreadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionAnswersThread, position: Int) = binding.apply {
            question.text = item.question.message
            voteUp.text = item.upVotes.size.toString()
            voteDown.text = item.downVotes.size.toString()
            rvAnswers.adapter = AnswersAdapter().also { it.submitList(item.answers) }
            rvAnswers.addItemDecoration(
                DividerItemDecoration(
                    rvAnswers.context,
                    (rvAnswers.layoutManager as LinearLayoutManager).orientation
                )
            )
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

    fun submitList(list: MutableList<QuestionAnswersThread>) {
        answers = list
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemCount(): Int {
        return answers.size
    }
}