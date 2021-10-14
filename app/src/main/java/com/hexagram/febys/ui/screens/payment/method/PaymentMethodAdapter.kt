package com.hexagram.febys.ui.screens.payment.method

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemPaymentMethodBinding
import com.hexagram.febys.models.view.PaymentMethod

class PaymentMethodAdapter : RecyclerView.Adapter<PaymentMethodAdapter.ItemPaymentMethodVH>() {
    private var paymentMethods = listOf<PaymentMethod>()

    var setAsDefault: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemPaymentMethodVH(
        ItemPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PaymentMethodAdapter.ItemPaymentMethodVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = paymentMethods.size

    fun submitList(list: List<PaymentMethod>) {
        paymentMethods = list
        notifyItemRangeChanged(0, list.size)
    }

    inner class ItemPaymentMethodVH(
        private val binding: ItemPaymentMethodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val paymentMethod = paymentMethods[position]
            binding.paymentMethod = paymentMethod
            binding.ivPayment.setImageResource(paymentMethod.img)

            root.setOnClickListener { setAsDefault(paymentMethod, position) }
        }

        private fun setAsDefault(paymentMethod: PaymentMethod, position: Int) {
            val defaultIndex = paymentMethods.indexOfFirst { it.isDefault }
            if (defaultIndex != -1) {
                paymentMethods[defaultIndex].isDefault = false
                notifyItemChanged(defaultIndex)
            }

            paymentMethods[position].isDefault = true
            notifyItemChanged(position)

            setAsDefault?.invoke(paymentMethod.id)
        }
    }
}