package com.hexagram.febys.db.converter

import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString()
    }

    @TypeConverter
    fun toStringList(listAsString: String): List<String> {
        return listAsString.split(",").toList()
    }
}