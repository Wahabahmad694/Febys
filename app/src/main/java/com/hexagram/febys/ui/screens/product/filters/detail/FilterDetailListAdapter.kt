package com.hexagram.febys.ui.screens.product.filters.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemFiltersDetailListBinding

class FilterDetailListAdapter : RecyclerView.Adapter<FilterDetailListAdapter.ViewHolder>() {
    private var detailList = listOf<String>()
    var selectedFilter = mutableSetOf<String>()
    var filterClickCallback: ((filter: String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemFiltersDetailListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilterDetailListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    fun submitList(values: List<String>, filter: MutableSet<String> = mutableSetOf("")) {
        selectedFilter = filter
        this.detailList = values
        notifyItemRangeChanged(0, values.size)
    }

    fun updateSelectedFilter(filter: String) {
        if (selectedFilter.contains(filter)) {
            selectedFilter.remove(filter)
        } else {
            selectedFilter.add(filter)
        }
        notifyItemRangeChanged(0, detailList.size)
    }

    fun clearSelectedFilter(filters: List<String>) {
        selectedFilter.removeAll(filters)
        notifyItemRangeChanged(0, detailList.size)
    }

    inner class ViewHolder(
        private val binding: ItemFiltersDetailListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val filterValue = detailList[position]
            labelRequiredOption.text = filterValue

            ivSelect.isVisible = filterValue in selectedFilter

            root.setOnClickListener {
                updateSelectedFilter(filterValue)
                filterClickCallback?.invoke(filterValue)
            }
        }
    }

}

