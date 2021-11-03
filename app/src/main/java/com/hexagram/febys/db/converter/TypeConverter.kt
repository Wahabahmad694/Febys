package com.hexagram.febys.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hexagram.febys.models.api.price.Price
import java.util.*

class TypeConverter {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString()
    }

    @TypeConverter
    fun toStringList(listAsString: String): List<String> {
        return listAsString.split(",").toList()
    }

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun priceToString(price: Price): String {
        return Gson().toJson(price)
    }

    @TypeConverter
    fun priceFromString(price: String): Price {
        return Gson().fromJson(price, Price::class.java)
    }
}