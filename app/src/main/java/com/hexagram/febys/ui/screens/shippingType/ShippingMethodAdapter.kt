package com.hexagram.febys.ui.screens.shippingType

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemShippingMethodBinding
import com.hexagram.febys.models.swoove.Estimate
import com.hexagram.febys.utils.capitalize
import com.hexagram.febys.utils.load

class ShippingMethodAdapter :
    RecyclerView.Adapter<ShippingMethodAdapter.ViewHolder>() {
    private var shippingMethods = mutableListOf<Estimate>()
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

    fun submitList(estimate: MutableList<Estimate>) {
        this.shippingMethods = estimate
        notifyItemRangeChanged(0, estimate.size)
    }


    inner class ViewHolder(
        private val binding: ItemShippingMethodBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {

            val estimates = shippingMethods[position]
            binding.apply {
                ivShippingMethod.load(estimates.estimateTypeDetails.icon)
                labelShippingMethodName.text = (estimates.estimateTypeDetails.name).capitalize()
                tvDeliveryDays.text = estimates.timeString
                tvShippingFee.text =
                    "${estimates.totalPricing.currency_code} ${estimates.totalPricing.value}"
                rbSelected.isChecked = estimates.selected
                if (estimates.selected) {
                    markAsSelected(binding.containerShippingMethod)
                } else {
                    markAsNotSelected(binding.containerShippingMethod)
                }

                rbSelected.setOnClickListener {
                    handleRbClick(position)

                }
                root.setOnClickListener {

                    handleRbClick(position)
                }


            }
        }
    }

    fun handleRbClick(position: Int)
    {
        shippingMethods.forEachIndexed { index, estimate ->
            shippingMethods[index].selected = position == index
        }
        notifyDataSetChanged()
    }

    fun markAsNotSelected(backgroundView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_grey)
    }

    fun markAsSelected(backgroundView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_red)
    }

    fun selectedItem() = shippingMethods.firstOrNull { it.selected }
}