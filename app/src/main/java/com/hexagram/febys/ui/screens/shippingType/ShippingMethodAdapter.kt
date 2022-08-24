package com.hexagram.febys.ui.screens.shippingType

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemShippingMethodBinding
import com.hexagram.febys.models.swoove.Estimate
import com.hexagram.febys.utils.load

class ShippingMethodAdapter : RecyclerView.Adapter<ShippingMethodAdapter.ViewHolder>() {
    private var shippingMethods = listOf<Estimate>()
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

    fun submitList(estimate: List<Estimate>) {
        this.shippingMethods = estimate
        notifyItemRangeChanged(0, estimate.size)
    }

    var onItemClick: ((method: Estimate) -> Unit)? = null

    inner class ViewHolder(
        private val binding: ItemShippingMethodBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {

            val estimates = shippingMethods[position]
            binding.apply {
                ivShippingMethod.load(estimates.estimateTypeDetails.icon)
                labelShippingMethodName.text = estimates.estimateTypeDetails.name
                tvDeliveryDays.text = estimates.timeString
                tvShippingFee.text = "${estimates.totalPricing.currency_code } ${estimates.totalPricing.value }"
            }
        }
    }

}