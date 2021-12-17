package com.hexagram.febys.ui.screens.product.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemFiltersBinding
import com.hexagram.febys.models.api.product.Attr


class FiltersAdapter : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {
    private var filters = listOf<Attr>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    var onItemClick: ((string: Attr) -> Unit)? = null
    override fun onBindViewHolder(holder: FiltersAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    fun submitList(filters: List<Attr>) {
        this.filters = filters
        notifyItemRangeChanged(0, filters.size)
    }

    inner class ViewHolder(
        private val binding: ItemFiltersBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {

            val filter = filters[position]
            labelFilterOption.text = filter.name
            root.setOnClickListener {
                onItemClick?.invoke((filter))
            }
        }
    }
}
