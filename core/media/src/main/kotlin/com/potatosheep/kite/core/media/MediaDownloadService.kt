package com.potatosheep.kite.core.media

import android.content.Context
import android.net.Uri
import java.io.IOException
import kotlin.jvm.Throws

interface MediaDownloadService {

    /**
     * Sets the HLS playlist URL.
     *
     * @param url URL of HLS playlist.
     *
     * @return [HLSUri] containing the URLs of the video and (if it exists; empty otherwise) audio
     * streams
     */
    @Throws(IOException::class)
    suspend fun setHLSPlaylist(url: String): HLSUri

    /**
     * Downloads a video.
     *
     * @param videoUrl URL of the MP4 file.
     * @param fileName name of the downloaded file.
     * @param uri URI of the directory to save the video and audio files.
     * @param context the application context.
     * @param isHLS if `videoUrl` is from a HLS playlist or not. Default is `true`.
     */
    @Throws(IOException::class)
    suspend fun downloadVideo(
        videoUrl: String,
        fileName: String,
        uri: Uri,
        context: Context,
        isHLS: Boolean = true,
    )

    /**
     * Downloads the audio of a HLS video.
     *
     * @param audioUrl URL of the audio file.
     * @param fileName name of the downloaded file.
     * @param uri URI of the directory to save the video and audio files.
     * @param context the application context.
     */
    @Throws(IOException::class)
    suspend fun downloadAudio(
        audioUrl: String,
        fileName: String,
        uri: Uri,
        context: Context,
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
    suspend fun downloadImage(
        imageUrl: String,
        fileName: String,
        uri: Uri,
        context: Context,
    )
}