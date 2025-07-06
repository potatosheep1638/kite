package com.potatosheep.kite.core.media

import android.content.Context
import android.net.Uri
import java.io.IOException
import kotlin.jvm.Throws

abstract class MediaDownloadService {

    protected var hlsVideoUrl = ""
    protected var hlsAudioUrl = ""

    /**
     * Sets the HLS playlist URL.
     *
     * @param url URL of HLS playlist.
     */
    abstract suspend fun setHLSPlaylist(url: String)

    /**
     * Downloads a video.
     *
     * @param fileName name of the downloaded file.
     * @param uri URI of the directory to save the video and audio files.
     * @param context the application context.
     * @param videoUrl URL of the MP4 file. This will take precedence over the HLS playlist url, if
     * it was set.
     */
    @Throws(IOException::class)
    abstract suspend fun downloadVideo(
        fileName: String,
        uri: Uri,
        context: Context,
        videoUrl: String = "",
    )

    /**
     * Downloads the audio of a HLS video.
     *
     * @param fileName name of the downloaded file.
     * @param uri URI of the directory to save the video and audio files.
     * @param context the application context.
     */
    @Throws(IOException::class)
    abstract suspend fun downloadAudio(
        fileName: String,
        uri: Uri,
        context: Context
    )

    /**
     * Downloads an image.
     *
     * @param imageUrl URL of the image file.
     * @param fileName name of the downloaded file.
     * @param uri URI of the directory to save the video and audio files.
     * @param context the application context.
     */
    @Throws(IOException::class)
    abstract suspend fun downloadImage(
        imageUrl: String,
        fileName: String,
        uri: Uri,
        context: Context
    )
}