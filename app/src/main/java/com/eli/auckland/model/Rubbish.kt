package com.eli.auckland.model

import com.squareup.moshi.Json

/**
 * {
    "Location":"1 Pohue Avenue, Huapai",
    "AssessmentNumber":"12340325328",
    "HRubbish":"There are no council-run rubbish collections in this area. This service is performed by independent waste operators. Check your bag or bin to see which operator makes your collections. ",
    "HRecycling":"Collection day: Friday, fortnightly except after a public holiday.",
    "HNext":[
        {
        "detail":"Friday 9 July",
        "from":"20210708 233000",
        "to":"20210709 065959"
        }
    ],
    "HComment":"Put bins out the night before or before 7am.",
    "CRubbish":"There are no council-run rubbish collections in this area. This service is performed by independent waste operators. Check your bag or bin to see which operator makes your collections. ",
    "CRecycling":"Collection day: Friday, fortnightly except after a public holiday.",
    "CNext":[
        {
        "detail":"Friday 9 July",
        "from":"20210708 233000",
        "to":"20210709 065959"
        }
    ],
    "CComment":"Put bins out the night before or before 7am."
}
 */
data class Rubbish(
    @Json(name = "Location")
    var location: String? = null,
    @Json(name = "AssessmentNumber")
    var assessmentNumber: String? = null,
    @Json(name = "HRubbish")
    var hRubbish: String? = null,
    @Json(name = "HRecycling")
    var hRecycling: String? = null,
    @Json(name = "HNext")
    /**
     * Household collection
     */
    var hNext: List<HCNext>? = null,
    @Json(name = "HComment")
    var hComment: String? = null,
    @Json(name = "CRubbish")
    var cRubbish: String? = null,
    @Json(name = "CRecycling")
    var cRecycling: String? = null,
    @Json(name = "CNext")
    /**
     * Commercial collection
     */
    var cNext: List<HCNext>? = null,
    @Json(name = "CComment")
    var cComment: String? = null
)