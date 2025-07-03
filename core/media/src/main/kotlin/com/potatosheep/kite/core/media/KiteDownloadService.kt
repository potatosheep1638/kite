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
) : MediaDownloadService {
    private val client = okHttpCallFactory.get()

    override suspend fun downloadHLS(
        playlistUrl: String,
        uri: Uri,
        context: Context
    ) {
        withContext(ioDispatcher) {
            // Get HLS playlist
            val playListRequest = Request.Builder().url(playlistUrl).build()
            val playlist: MutableList<String> = mutableListOf()

            client.newCall(playListRequest).execute().use { response ->
                val body = requireNotNull(response.body())
                playlist.addAll(body.byteStream().readAllLines())
            }

            // Parse playlist to get audio URI
            val uris = HLSParser.parsePlaylist(playlist)
            val audioUrl = "${playlistUrl.substring(0, playlistUrl.lastIndexOf('/')+1)}${uris.audio}"

            // Get the URI of the AAC file
            val audioRequest = Request.Builder().url(audioUrl).build()

            var aacFileURI: String
            client.newCall(audioRequest).execute().use { response ->
                val body = requireNotNull(response.body())
                aacFileURI = HLSParser.parseM3U8(body.byteStream().readAllLines())
            }

            if (aacFileURI.isEmpty()) throw IllegalArgumentException("No valid URI found")

            // Create the MP4 file (empty)
            val treeUri = context.contentResolver.persistedUriPermissions[0].uri

            val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )

            val videoFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                folderUri,
                "video/mp4",
                "video.mp4"
            )

            // Get the MP4 file from the network
            val mp4ParentPath = playlistUrl
                .substring(0, playlistUrl.lastIndexOf('/')+1)
                .replace("hls", "vid")
            val mp4Uri = "${uris.video.split("_", ".")[1]}.mp4"
            val mp4Url =  "$mp4ParentPath$mp4Uri"

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

            // Create the AAC file (empty)
            val audioFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                folderUri,
                "audio/aac",
                "audio.aac"
            )

            // Get the AAC file itself
            val aacFileUrl = "${audioUrl.substring(0, audioUrl.lastIndexOf('/') + 1)}$aacFileURI"
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
}