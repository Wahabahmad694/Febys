package com.hexagram.febys.ui.screens.product.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemRvAnswersBinding
import com.hexagram.febys.models.view.Thread

class AnswersAdapter : RecyclerView.Adapter<AnswersAdapter.AnswersVH>() {
    private var answers = mutableListOf<Thread>()

    inner class AnswersVH(
        private val binding: ItemRvAnswersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Thread, position: Int) = binding.apply {
            answer.text = item.message
            name.text = item.senderName
            date.text = item.timeStamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswersVH {
        return AnswersVH(
            ItemRvAnswersBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AnswersVH, position: Int) {
        holder.bind(answers[position], position)
    }

    fun submitList(list: MutableList<Thread>) {
        answers = list
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemCount(): Int {
        return answers.size
    }
}