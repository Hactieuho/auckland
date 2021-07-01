package com.eli.auckland.model

import com.squareup.moshi.Json
import java.util.*

data class HCNext (
    @Json(name = "detail")
    var detail: String? = null,
    @Json(name = "from")
    var from: Date? = null,
    @Json(name = "to")
    var to: Date? = null
)