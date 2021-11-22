package com.hexagram.febys.ui.screens.vouchers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemVoucherBinding
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.toFixedDecimal

class VouchersAdapter : RecyclerView.Adapter<VouchersAdapter.ViewHolder>() {
    private var vouchers = listOf<Voucher>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return vouchers.size
    }

    fun submitList(vouchers: List<Voucher>) {
        this.vouchers = vouchers
        notifyItemRangeChanged(0, vouchers.size)
    }

    inner class ViewHolder(
        private val binding: ItemVoucherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            card.preventCornerOverlap = false

            val voucher = vouchers[position]
            voucherType.text = voucher.voucher.amountType
            voucherCode.text = voucher.voucher.code
            voucherExpirationDate.text = Utils.formatDate(voucher.voucher.endingTime)
            voucherAmount.text = root.context.getString(
                R.string.price_with_dollar_sign, voucher.voucher.amount.toFixedDecimal(2)
            )

            background.setImageURI("res:///${R.drawable.bg_voucher}")
        }
    }

}