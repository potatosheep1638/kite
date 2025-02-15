package com.potatosheep.kite.core.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlairComponent(
    val value: String,
    val type: FlairComponentType
)

enum class FlairComponentType(val value: String) {
    EMOJI("emoji"),
    TEXT("text")
}