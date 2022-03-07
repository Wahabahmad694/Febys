package com.hexagram.febys.ui.screens.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.databinding.ItemNotificationsBinding
import com.hexagram.febys.models.api.notification.RemoteNotification
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
import com.hexagram.febys.utils.setBackgroundCircularColor


class NotificationAdapter :
    PagingDataAdapter<RemoteNotification, NotificationAdapter.NotificationViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RemoteNotification>() {

            override fun areItemsTheSame(
                oldItem: RemoteNotification, newItem: RemoteNotification
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RemoteNotification, newItem: RemoteNotification
            ): Boolean {
                return oldItem.title == newItem.title
                        && oldItem.body == newItem.body
                        && oldItem.data == newItem.data
            }
        }
    }

    var onItemClick: ((remoteNotification: RemoteNotification) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class NotificationViewHolder(
        private val binding: ItemNotificationsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val notification = getItem(position)
            tvName.text = notification?.title
            tvDescription.text = notification?.body
            notification?.getNotification()?.sentAt?.let {
                tvDateTime.text = Utils.DateTime.formatDate(it, FORMAT_MONTH_DATE_YEAR_HOUR_MIN)
            }
            notification?.getNotification()?.getColor()?.let {
                bgNotificationLogo.setBackgroundCircularColor(it)
            }
            notification?.getNotification()?.getIcon()?.let {
                ivNotifyLogo.setImageResource(it)
            }

            root.setOnClickListener {
                if (notification != null) onItemClick?.invoke(notification)
            }
        }
    }
}
