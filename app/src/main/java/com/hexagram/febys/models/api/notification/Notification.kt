package com.hexagram.febys.models.api.notification

import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.R
import com.hexagram.febys.utils.OrderStatus

data class RemoteNotification(
    val _id: String,
    var read: Boolean,
    val title: String,
    val body: String,
    var data: JsonObject,
) {
    fun getNotification(): Notification? {
        val notificationType = when (data["type"].asString) {
            "febys-plus" -> {
                Notification.FebysPlus::class.java
            }
            "consumer" -> {
                Notification.Consumer::class.java
            }
            "order" -> {
                Notification.Order::class.java
            }
            "question-answers" -> {
                Notification.QA::class.java
            }
            else -> {
                return null
            }
        }

        return Gson().fromJson(data, notificationType)
    }
}

sealed class Notification(
    val type: String,
    @SerializedName("sended_at")
    val sentAt: String,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Notification? {
            return when (map["type"] as String) {
                "febys-plus" -> {
                    FebysPlus(
                        map["type"] as String,
                        map["sended_at"] as String
                    )
                }
                "consumer" -> {
                    Consumer(
                        map["type"] as String,
                        map["sended_at"] as String
                    )
                }
                "order" -> {
                    Order(
                        map["type"] as String,
                        map["sended_at"] as String,
                        map["order_id"] as String,
                        map["status"] as? String?
                    )
                }
                "question-answers" -> {
                    QA(
                        map["type"] as String,
                        map["sended_at"] as String,
                        map["message"] as String,
                        map["thread_id"] as String,
                        map["product_id"] as String,
                        map["consumer_name"] as String,
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

    abstract fun getColor(): Int

    abstract fun getIcon(): Int

    class FebysPlus(
        type: String,
        sentAt: String,
    ) : Notification(type, sentAt) {

        override fun getColor(): Int {
            return Color.parseColor("#FFCFCF")
        }

        override fun getIcon(): Int {
            return R.drawable.ic_febys_plus_notification
        }
    }


    class Consumer(
        type: String,
        sentAt: String,
    ) : Notification(type, sentAt) {

        override fun getColor(): Int {
            return Color.parseColor("#F7F7F7")
        }

        override fun getIcon(): Int {
            return R.drawable.ic_febys_notification
        }
    }

    class Order(
        type: String,
        sentAt: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("status")
        val _status: String?,
    ) : Notification(type, sentAt) {
        val status
            get() = _status ?: "PENDING"

        override fun getColor(): Int {
            return when (status) {
                OrderStatus.RETURNED,
                OrderStatus.PENDING_RETURN,
                -> {
                    Color.parseColor("#E0E7FF")
                }
                OrderStatus.DELIVERED -> {
                    Color.parseColor("#D1FFB5")
                }
                else -> {
                    OrderStatus.getStatusColor(status)
                }
            }
        }

        override fun getIcon(): Int {
            return R.drawable.ic_order_notification
        }
    }

    class QA(
        type: String,
        sentAt: String,
        message: String,
        @SerializedName("thread_id")
        val threadId: String,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("consumer_name")
        val consumerName: String,
    ) : Notification(type, sentAt) {

        override fun getColor(): Int {
            return Color.parseColor("#C8FCFF")
        }

        override fun getIcon(): Int {
            return R.drawable.ic_qa_notification
        }
    }
}