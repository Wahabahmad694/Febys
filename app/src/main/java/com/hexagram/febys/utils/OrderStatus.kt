package com.hexagram.febys.utils

import android.graphics.Color

object OrderStatus {
    const val PENDING = "PENDING"
    const val CANCELED = "CANCELED"
    const val REJECTED = "REJECTED"
    const val ACCEPTED = "ACCEPTED"
    const val CANCELLED_BY_VENDOR = "CANCELLED_BY_VENDOR"
    const val SHIPPED = "SHIPPED"
    const val DELIVERED = "DELIVERED"
    const val RETURNED = "RETURNED"
    const val PENDING_RETURN = "PENDING_RETURN"
    const val REVIEWED = "REVIEWED"

    fun getStatusForDisplay(status: String?): String {
        return when (status) {
            PENDING_RETURN, PENDING -> {
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
            PENDING_RETURN -> {
                "#F7F7F7"
            }
            RETURNED -> {
                "#222222"
            }
            DELIVERED -> {
                "#B2FFDA"
            }
            else -> {
                "#FFFFFF"
            }
        }

        return Color.parseColor(color)
    }

    fun isAccepted(status: String?): Boolean {
        return when (status) {
            PENDING,
            REJECTED -> false
            else -> true
        }
    }

    fun isShipped(status: String?): Boolean {
        return when (status) {
            PENDING,
            CANCELED,
            REJECTED,
            CANCELLED_BY_VENDOR,
            ACCEPTED -> false
            else -> true
        }
    }

    fun isReceived(status: String?): Boolean {
        return when (status) {
            PENDING,
            CANCELED,
            REJECTED,
            CANCELLED_BY_VENDOR,
            ACCEPTED,
            SHIPPED-> false
            else -> true
        }
    }
}