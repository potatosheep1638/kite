package com.potatosheep.kite.core.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaLink (
    val link: String,
    val caption: String?,
    val mediaType: MediaType
)

enum class MediaType {
    IMAGE,
    GALLERY_THUMBNAIL,
    GALLERY_LINK,
    GALLERY_IMAGE,
    ARTICLE_THUMBNAIL,
    ARTICLE_LINK,
    VIDEO,
    VIDEO_THUMBNAIL
}