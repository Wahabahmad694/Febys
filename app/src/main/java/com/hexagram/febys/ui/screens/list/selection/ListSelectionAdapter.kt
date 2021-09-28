package com.hexagram.febys.ui.screens.list.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemListSelectionBinding

class ListSelectionAdapter : RecyclerView.Adapter<ListSelectionAdapter.ListItemsViewHolder>() {
    private var selectedItem = ""
    private var items = emptyList<String>()

    var interaction: ((variant: String) -> Unit)? = null

    inner class ListItemsViewHolder(
        private val binding: ItemListSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, position: Int) {
            binding.root.setOnClickListener {
                if (item == selectedItem) return@setOnClickListener
                interaction?.invoke(item)
                updateSelectedItem(item)
            }

            binding.tvSelection.text = item

            val style =
                if (item == selectedItem) R.style.TextTheme_HelveticaNeue_TS14_Bold
                else R.style.TextTheme_HelveticaNeue_TS14_Grey

            binding.tvSelection.setTextAppearance(style)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemsViewHolder {
        return ListItemsViewHolder(
            ItemListSelectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ListItemsViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    fun submitList(
        selectedItem: String, items: List<String>,
    ) {
        this.selectedItem = selectedItem
        this.items = items
        notifyDataSetChanged()
    }

    fun submitList(items: List<String>) {
        this.selectedItem = items.firstOrNull() ?: ""
        this.items = items
        notifyDataSetChanged()
    }

    fun updateSelectedItem(selectedItem: String) {
        this.selectedItem = selectedItem
        notifyDataSetChanged()
    }

    fun getSelectedItem() = selectedItem
}