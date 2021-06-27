package com.eli.auckland.model

import com.squareup.moshi.Json
import java.io.Serializable

data class AddressResult (
    @Json(name = "address")
    var address: List<Address>? = null
) : Serializable {
    fun isSuccessful() = !address.isNullOrEmpty()
}