package com.hexagram.febys.ui.screens.shipping.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemShippingAddressBinding
import com.hexagram.febys.models.view.ShippingAddress

class ShippingAddressAdapter :
    ListAdapter<ShippingAddress, ShippingAddressAdapter.ShippingAddressViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShippingAddress>() {
            override fun areItemsTheSame(
                oldItem: ShippingAddress, newItem: ShippingAddress
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ShippingAddress, newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    var setAsDefault: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingAddressViewHolder {
        return ShippingAddressViewHolder(
            ItemShippingAddressBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShippingAddressViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private fun setAsDefault(shippingAddress: ShippingAddress, position: Int) {
        val defaultIndex = currentList.indexOfFirst { it.isDefault }
        if (defaultIndex != -1) {
            currentList[defaultIndex].isDefault = false
            notifyItemChanged(defaultIndex)
        }

        currentList[position].isDefault = true
        notifyItemChanged(position)

        setAsDefault?.invoke(shippingAddress.id)
    }

    inner class ShippingAddressViewHolder(
        private val binding: ItemShippingAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shippingAddress: ShippingAddress, position: Int) {
            binding.address = shippingAddress

            binding.setAsDefault.setOnClickListener {
                if (!shippingAddress.isDefault) {
                    setAsDefault(shippingAddress, position)
                }
            }
        }
    }
}