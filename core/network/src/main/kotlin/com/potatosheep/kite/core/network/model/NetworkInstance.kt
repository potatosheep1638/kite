package com.potatosheep.kite.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkInstance(
    @Json(name = "url")
    val url: String?,

    @Json(name = "version")
    val version: String
)