package com.hexagram.febys.utils

import android.graphics.Color

object OrderStatus {
    const val PENDING = "PENDING"
    const val ACCEPTED = "ACCEPTED"
    const val CANCELLED_BY_VENDOR = "CANCELLED_BY_VENDOR"
    const val SHIPPED = "SHIPPED"
    const val CANCELED = "CANCELED"
    const val REJECTED = "REJECTED"
    const val RETURNED = "RETURNED"
    const val PENDING_RETURN = "PENDING_RETURN"
    const val DELIVERED = "DELIVERED"

    fun getStatusForDisplay(status: String?): String {
        return when (status) {
            PENDING -> {
                "Pending"
            }
            ACCEPTED -> {
                "Processing"
            }
            CANCELLED_BY_VENDOR -> {
                "Canceled by vendor"
            }
            SHIPPED -> {
                "Shipped"
            }
            CANCELED -> {
                "Canceled"
            }
            REJECTED -> {
                "Rejected"
            }
            RETURNED -> {
                "Returned"
            }
            PENDING_RETURN -> {
                "Pending return"
            }
            DELIVERED -> {
                "Delivered"
            }
            else -> {
                ""
            }
        }
    }

    fun getStatusColor(status: String?): Int {
        val color = when (status) {
            PENDING -> {
                "#888888"
            }
            ACCEPTED -> {
                "#70ACF2"
            }
            CANCELLED_BY_VENDOR -> {
                "#FFA24A"
            }
            SHIPPED -> {
                "#FF976B"
            }
            CANCELED -> {
                "#FF4545"
            }
            REJECTED -> {
                "#DE0407"
            }
            RETURNED -> {
                "#FF976B"
            }
            PENDING_RETURN -> {
                "#FF976B"
            }
            DELIVERED -> {
                "#78D971"
            }
            else -> {
                "#00000000"
            }
        }

        return Color.parseColor(color)
    }
}