package com.potatosheep.kite.core.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadData(
    val mediaUrl: String,
    val filename: String,
    val contentUri: String,
    val flags: Int
) : Parcelable {
    companion object {
        const val IS_IMAGE = 0x00000001
        const val IS_VIDEO = 0x00000002
        const val IS_HLS = 0x00000004
    }
}