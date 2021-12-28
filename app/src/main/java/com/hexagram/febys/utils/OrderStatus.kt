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
                "Received"
            }
            else -> {
                ""
            }
        }
    }

    fun getStatusColor(status: String?): Int {
        val color = when (status) {
            PENDING -> {
                "#FFF5D3"
            }
            ACCEPTED -> {
                "#CEE5FF"
            }
            CANCELLED_BY_VENDOR -> {
                "#FFDBB9"
            }
            SHIPPED -> {
                "#FFFBA5"
            }
            CANCELED -> {
                "#DBDBDB"
            }
            REJECTED -> {
                "#FFCFCF"
            }
            RETURNED,
            PENDING_RETURN -> {
                "#E0E7FF"
            }
            DELIVERED -> {
                "#B2FFDA"
            }
            else -> {
                "#000000"
            }
        }

        return Color.parseColor(color)
    }
}