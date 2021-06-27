package com.eli.auckland.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Address (
    @Json(name = "id")
    var id: String? = null,
    @Json(name = "name")
    var name: String? = null
) : Serializable