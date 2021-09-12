package com.liz.auckland.api

import com.liz.auckland.di.SimpleDateFormat1
import com.liz.auckland.di.SimpleDateFormat2
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class MoshiUTCDateAdapter @Inject constructor(
    @SimpleDateFormat1 val dateTimeFormat1: DateFormat,
    @SimpleDateFormat2 val dateTimeFormat2: DateFormat
) : JsonAdapter<Date>() {

    override fun fromJson(reader: JsonReader): Date? {
        val dateAsString = reader.nextString()
        var result: Date? = null
        try {
            result = synchronized(dateTimeFormat1) {
                dateTimeFormat1.parse(dateAsString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (result == null) {
            try {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                result = synchronized(dateTimeFormat2) {
                    dateTimeFormat2.parse("$dateAsString $year")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (result == null) {
            try {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                result = synchronized(dateTimeFormat2) {
                    dateTimeFormat2.parse("$dateAsString ${year + 1}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateTimeFormat1) {
                writer.value(value.toString())
            }
        }
    }
}