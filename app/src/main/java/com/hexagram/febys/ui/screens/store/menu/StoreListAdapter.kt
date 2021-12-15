package com.hexagram.febys.ui.screens.store.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemStoreListBinding
import com.hexagram.febys.models.api.menu.StoreMenus

class StoreListAdapter : RecyclerView.Adapter<StoreListAdapter.ViewHolder>() {
    private var storeMenus = listOf<StoreMenus>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = storeMenus.size

    fun submitList(menuList: List<StoreMenus>) {
        this.storeMenus = menuList
        notifyItemRangeChanged(0, menuList.size)
    }

    inner class ViewHolder(
        private val binding: ItemStoreListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val menuList = storeMenus[position]
            tvNameStore.text = menuList.name
        }
    }
}