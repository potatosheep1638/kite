package com.potatosheep.kite.core.media

import android.content.Context
import android.net.Uri

interface MediaDownloadService {

    /**
     * Downloads the video and audio files.
     *
     * @param videoUrl URL of the video file
     * @param audioUrl URL of the audio file
     * @param uri URI of the directory to save the video and audio files
     * @param context the application context
     */
    suspend fun downloadHLS(
        playlistUrl: String,
        uri: Uri,
        context: Context,
    )
}