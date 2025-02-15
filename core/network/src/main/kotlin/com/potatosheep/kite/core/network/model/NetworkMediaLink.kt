package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.MediaLink
import com.potatosheep.kite.core.model.MediaType

data class NetworkMediaLink(
    val link: String,
    val caption: String?,
    val mediaType: MediaType
)

fun NetworkMediaLink.toExternalModel() = MediaLink(
    link = link,
    caption = caption,
    mediaType = mediaType
)