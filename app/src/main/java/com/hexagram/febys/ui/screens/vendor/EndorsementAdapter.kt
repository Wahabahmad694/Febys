package com.hexagram.febys.ui.screens.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemMyEndorsementsBinding
import com.hexagram.febys.models.view.Endorsement
import com.hexagram.febys.utils.load

class EndorsementAdapter : RecyclerView.Adapter<EndorsementAdapter.EndorsementViewHolder>() {
    private var endorsements = emptyList<Endorsement>()

    inner class EndorsementViewHolder(
        private val binding: ItemMyEndorsementsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(endorsement: Endorsement, position: Int) {
            binding.apply {
                profileImg.load(endorsement.profileImage)
                name.text = endorsement.name
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EndorsementAdapter.EndorsementViewHolder {
        return EndorsementViewHolder(
            ItemMyEndorsementsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: EndorsementAdapter.EndorsementViewHolder, position: Int) {
        holder.bind(endorsements[position], position)
    }

    fun submitList(list: List<Endorsement>) {
        endorsements = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = endorsements.size
}