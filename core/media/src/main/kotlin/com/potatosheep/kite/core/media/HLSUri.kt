package com.potatosheep.kite.core.media

/**
 * A carrier class containing the URIs of the video and audio streams for a given HLS video.
 *
 * @property video the video URI
 * @property audio the audio URI
 */
data class HLSUri(
    val video: String,
    val audio: String,
)