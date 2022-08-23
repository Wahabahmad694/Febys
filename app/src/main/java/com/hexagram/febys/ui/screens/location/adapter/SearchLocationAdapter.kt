package com.hexagram.yahuda.ui.activity.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.hexagram.febys.databinding.SearchLocationRowBinding


class SearchLocationAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AutocompletePrediction>() {

        override fun areItemsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {

            return oldItem.placeId == newItem.placeId
        }

        override fun areContentsTheSame(
            oldItem: AutocompletePrediction,
            newItem: AutocompletePrediction
        ): Boolean {
            return oldItem.placeId == newItem.placeId
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SearchLocationVH(
            SearchLocationRowBinding.inflate(LayoutInflater.from(parent.context)),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchLocationVH -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<AutocompletePrediction>) {
        differ.submitList(list)
    }

    class SearchLocationVH
    constructor(
        private val itemBinding: SearchLocationRowBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: AutocompletePrediction) {

            itemBinding.locationNameTv.text = item.getFullText(null)

            itemBinding.locationNameTv.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: AutocompletePrediction)
    }
}