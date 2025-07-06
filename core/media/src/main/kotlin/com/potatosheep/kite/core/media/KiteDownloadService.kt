package com.potatosheep.kite.core.media

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.media.util.readAllLines
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
import okio.buffer
import okio.sink
import javax.inject.Inject

class KiteDownloadService @Inject constructor(
    okHttpCallFactory: dagger.Lazy<Call.Factory>,
    @Dispatcher(KiteDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MediaDownloadService() {
    private val client = okHttpCallFactory.get()

    override suspend fun setHLSPlaylist(url: String) {
        withContext(ioDispatcher) {
            // Get HLS playlist
            val playListRequest = Request.Builder().url(url).build()
            val playlist: MutableList<String> = mutableListOf()

            client.newCall(playListRequest).execute().use { response ->
                val body = requireNotNull(response.body())
                playlist.addAll(body.byteStream().readAllLines())
            }

            // Parse playlist to get audio URI
            val uris = HLSParser.parsePlaylist(playlist)
            val parentPath = url.substring(0, url.lastIndexOf('/')+1)

            hlsVideoUrl = "$parentPath${uris.video}"
            hlsAudioUrl = "$parentPath${uris.audio}"
        }
    }

    override suspend fun downloadVideo(
        fileName: String,
        uri: Uri,
        context: Context,
        videoUrl: String
    ) {
        withContext(ioDispatcher) {
            if (fileName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

            // Get the directory URI
            val treeUri = context.contentResolver.persistedUriPermissions[0].uri

            val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )

            // Create the MP4 file (empty)
            val videoFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                folderUri,
                "video/mp4",
                fileName
            )

            // Get the MP4 file from the network
            val mp4Url: String

            if (videoUrl.isEmpty()) {
                if (hlsVideoUrl.isEmpty()) throw IllegalStateException("HLS video URL cannot be empty")

                val parentPath = hlsVideoUrl
                    .substring(0, hlsVideoUrl.lastIndexOf('/')+1)
                    .replace("hls", "vid")
                val videoM3U8 = hlsVideoUrl.substring(hlsVideoUrl.lastIndexOf('/')+1)
                val mp4File = "${videoM3U8.split("_", ".")[1]}.mp4"

                mp4Url = "$parentPath$mp4File"
            } else {
                mp4Url = videoUrl
            }

            val mp4Request = Request.Builder().url(mp4Url).build()

            client.newCall(mp4Request).execute().use { response ->
                val body = requireNotNull(response.body())

                context.contentResolver.openOutputStream(videoFileUri!!)?.let { outputStream ->
                    val sink = outputStream.sink().buffer()

                    sink.writeAll(body.source())
                    sink.flush()
                    sink.close()
                }
            }
        }
    }

    override suspend fun downloadAudio(fileName: String, uri: Uri, context: Context) {
        withContext(ioDispatcher) {
            if (fileName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

            // Get the URI of the directory to write to
            val treeUri = context.contentResolver.persistedUriPermissions[0].uri

            val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )

            // Create the AAC file (empty)
            val audioFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                folderUri,
                "audio/aac",
                fileName
            )

            // Get the name of the AAC file
            val audioUrl = this@KiteDownloadService.hlsAudioUrl

            if (audioUrl.isEmpty()) throw IllegalStateException("HLS audio URL cannot be empty")

            val audioRequest = Request.Builder().url(audioUrl).build()

            var aacFile: String
            client.newCall(audioRequest).execute().use { response ->
                val body = requireNotNull(response.body())
                aacFile = HLSParser.parseM3U8(body.byteStream().readAllLines())
            }

            if (aacFile.isEmpty()) throw IllegalArgumentException("No audio file found")

            // Get the AAC file itself
            val aacFileUrl = "${audioUrl.substring(0, audioUrl.lastIndexOf('/') + 1)}$aacFile"
            val aacRequest = Request.Builder().url(aacFileUrl).build()

            client.newCall(aacRequest).execute().use { response ->
                val body = requireNotNull(response.body())

                context.contentResolver.openOutputStream(audioFileUri!!)?.let { outputStream ->
                    val sink = outputStream.sink().buffer()

                    sink.writeAll(body.source())
                    sink.flush()
                    sink.close()
                }
            }
        }
    }

    override suspend fun downloadImage(
        imageUrl: String,
        fileName: String,
        uri: Uri,
        context: Context
    ) {
        withContext(ioDispatcher) {
            val request = Request.Builder().url(imageUrl).build()

            client.newCall(request).execute().use { response ->
                val body = requireNotNull(response.body())

                context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                    val sink = outputStream.sink().buffer()

                    sink.writeAll(body.source())
                    sink.flush()
                    sink.close()
                }
            }
        }
    }
}