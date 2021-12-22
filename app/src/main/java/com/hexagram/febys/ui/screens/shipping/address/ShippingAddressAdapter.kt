package com.hexagram.febys.ui.screens.shipping.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemShippingAddressBinding
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress

class ShippingAddressAdapter :
    ListAdapter<ShippingAddress, ShippingAddressAdapter.ShippingAddressViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShippingAddress>() {
            override fun areItemsTheSame(
                oldItem: ShippingAddress, newItem: ShippingAddress
            ): Boolean {
                return oldItem.shippingDetail.shippingDetailId == newItem.shippingDetail.shippingDetailId
            }

            override fun areContentsTheSame(
                oldItem: ShippingAddress, newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    var setAsDefault: ((shippingAddress: ShippingAddress) -> Unit)? = null
    var editShippingAddress: ((shippingAddress: ShippingAddress) -> Unit)? = null
    var deleteShippingAddress: ((shippingAddress: ShippingAddress) -> Unit)? = null
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

    private fun setAsDefault(shippingAddress: ShippingAddress) {
        shippingAddress.shippingDetail.isDefault = true
        setAsDefault?.invoke(shippingAddress)
    }

    inner class ShippingAddressViewHolder(
        private val binding: ItemShippingAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shippingAddress: ShippingAddress, position: Int) {
            binding.address = shippingAddress

            binding.root.setOnClickListener {
                if (!shippingAddress.shippingDetail.isDefault) {
                    setAsDefault(shippingAddress)
                }
            }

            binding.editAddress.setOnClickListener {
                editShippingAddress?.invoke(shippingAddress)

            }
            binding.deleteAddress.setOnClickListener {
                deleteShippingAddress?.invoke(shippingAddress)
            }
        }
    }
}