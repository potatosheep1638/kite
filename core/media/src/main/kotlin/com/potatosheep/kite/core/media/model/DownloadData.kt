package com.potatosheep.kite.core.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadData(
    val mediaUrl: String,
    val filename: String,
    val contentUri: String,
    val id: String,
    val flags: Int
) : Parcelable {
    companion object {
        /**
         * Tells the download service that the media file to be downloaded is an image.
         */
        const val IS_IMAGE = 0x00000001

        /**
         * Tells the download service that the media file to be downloaded is a video. Set this flag
         * with the [IS_HLS] flag if the video uses HLS.
         */
        const val IS_VIDEO = 0x00000002

        /**
         * If set with the [IS_VIDEO] flag, tells the download service that the video file to be
         * downloaded is a HLS video.
         */
        const val IS_HLS = 0x00000004
    }
}