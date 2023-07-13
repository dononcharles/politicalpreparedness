package com.udacity.politicalpreparedness.data.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter {
    @FromJson
    fun dateFromJson(date: String): Date? {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return SimpleDateFormat("yyy-MM-dd", Locale.US).format(date)
    }
}
