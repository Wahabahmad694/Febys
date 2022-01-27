package com.hexagram.febys.ui.screens.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemTransactionsBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
import com.hexagram.febys.utils.setBackgroundRoundedColor

class TransactionPagerAdapter :
    PagingDataAdapter<Transaction, TransactionPagerAdapter.TransactionsViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Transaction>() {

            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem._id == newItem._id
            }
        }
    }

    var interaction: Interaction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        return TransactionsViewHolder(
            ItemTransactionsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return, position)

    }

    inner class TransactionsViewHolder(
        private val binding: ItemTransactionsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction, position: Int) = with(binding) {
            root.setOnClickListener {
                interaction?.onItemSelected(position, item)
            }

            transactionId.text = item._id
            tvSource.text = item.source
            tvDateTime.text =
                Utils.DateTime.formatDate(item.createdAt, FORMAT_MONTH_DATE_YEAR_HOUR_MIN)
            tvAmount.text =
                Price("", item.requestedAmount, item.requestedCurrency).getFormattedPrice()
            tvPurpose.text = item.purpose.replace("_", " ")
            tvStatus.text = item.status.substringBefore("_")
            tvStatus.setBackgroundRoundedColor(item.statusColor)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Transaction)
    }
}
