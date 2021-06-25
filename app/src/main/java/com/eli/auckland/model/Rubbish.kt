package com.eli.auckland.model

import com.squareup.moshi.Json

class Rubbish {
    @Json(name = "CNext")
    var cNext: List<String?>? = null

    @Json(name = "CRecycling")
    var cRecycling: String? = null

    @Json(name = "CRubbish")
    var cRubbish: String? = null

    @Json(name = "HNext")
    var hNext: List<String?>? = null

    @Json(name = "HRecycling")
    var hRecycling: String? = null

    @Json(name = "HRubbish")
    var hRubbish: String? = null

    @Json(name = "Location")
    var location: String? = null
}