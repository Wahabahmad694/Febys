package com.hexagram.febys.ui.screens.shippingType

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemShippingMethodBinding

class ShippingMethodAdapter : RecyclerView.Adapter<ShippingMethodAdapter.ViewHolder>() {
    private var shippingMethods = listOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemShippingMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ShippingMethodAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return shippingMethods.size
    }

    inner class ViewHolder(
        private val binding: ItemShippingMethodBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {


        }
    }

}