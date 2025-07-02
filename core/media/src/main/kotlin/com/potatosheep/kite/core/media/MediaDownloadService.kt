package com.potatosheep.kite.core.media

import android.content.Context
import android.net.Uri

interface MediaDownloadService {

    /**
     * Retrieves the HLS playlist file, parses it, and returns the URIs for the video and audio
     * files.
     *
     * Note that this method returns the first `#EXT-X-STREAM-INF` entry as the video link and the
     * URI field of the last `#EXT-X-MEDIA` entry as the audio link. The assumption being made here
     * is that both are the highest quality streams.
     *
     * @param playlistUrl the URL of the playlist
     *
     * @return [HLSLink] object contain the video and audio URIs
     */
    suspend fun getHLSPlaylist(
        playlistUrl: String
    ): HLSLink

    /**
     * Downloads the video and audio files.
     *
     * @param videoUrl URL of the video file
     * @param audioUrl URL of the audio file
     * @param uri URI of the directory to save the video and audio files
     * @param context the application context
     */
    suspend fun downloadHLS(
        videoUrl: String,
        audioUrl: String,
        uri: Uri,
        context: Context,
    )
}