package com.liz.auckland.model

import com.liz.auckland.util.formatDate
import com.liz.auckland.util.formatTime
import com.squareup.moshi.Json
import java.util.*

data class HCNext (
    @Json(name = "detail")
    var detail: Date? = null,
    @Json(name = "from")
    var from: Date? = null,
    @Json(name = "to")
    var to: Date? = null
) {
    fun fromDate() = from?.formatDate()
    fun fromTime() = from?.formatTime()
    fun fromDateTime() = "${fromDate()}\n${fromTime()}"
    fun toDate() = to?.formatDate()
    fun toTime() = to?.formatTime()
    fun toDateTime() = "${toDate()}\n${toTime()}"
}