package com.hexagram.febys.ui.screens.product.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemFiltersBinding


class SearchFilterAdapter : RecyclerView.Adapter<SearchFilterAdapter.ViewHolder>() {
    private var filters = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    var onItemClick: ((string: String) -> Unit)? = null
    override fun onBindViewHolder(holder: SearchFilterAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    fun submitList(filters: List<String>) {
        this.filters = filters
        notifyItemRangeChanged(0, filters.size)
    }

    inner class ViewHolder(
        private val binding: ItemFiltersBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {

            val filter = filters[position]
            labelFilterOption.text = filter
            root.setOnClickListener {
                onItemClick?.invoke((filter))
            }
        }
    }
}
