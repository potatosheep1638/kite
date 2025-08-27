package com.potatosheep.kite.core.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.app.ServiceCompat
import androidx.core.net.toUri
import androidx.core.util.set
import androidx.core.util.valueIterator
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.common.constants.IntentData
import com.potatosheep.kite.core.media.model.DownloadData
import com.potatosheep.kite.core.media.util.readAllLines
import com.potatosheep.kite.core.notification.Notifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
import okio.FileNotFoundException
import okio.buffer
import okio.sink
import javax.inject.Inject

@AndroidEntryPoint
class KiteDownloadService : LifecycleService() {
    @Inject
    lateinit var okHttpCallFactory: dagger.Lazy<Call.Factory>

    @Inject
    @Dispatcher(KiteDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var notifier: Notifier

    private var downloadQueue = SparseBooleanArray()

    override fun onCreate() {
        super.onCreate()
        startService()
        Log.d("KiteDownloadService", "Service started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("KiteDownloadService", "Service stopped")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val downloadData: DownloadData? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(IntentData.DOWNLOAD_DATA, DownloadData::class.java)
            } else {
                intent?.getParcelableExtra(IntentData.DOWNLOAD_DATA)
            }

        if (downloadData == null) {
            throw IllegalArgumentException("DownloadData cannot be null")
        } else {
            val downloadId = downloadData.id.hashCode()
            downloadQueue[downloadId] = true

            val contentUri = downloadData.contentUri.toUri()

            lifecycleScope.launch {
                when (downloadData.flags) {
                    DownloadData.IS_IMAGE -> {
                        val imageFilename = contentUri.getFilename()

                        downloadNotify(
                            imageFilename,
                            downloadId,
                            Notifier.STATE_DOWNLOADING_IMAGE,
                        )

                        downloadImage(
                            imageUrl = downloadData.mediaUrl,
                            uri = contentUri,
                            context = applicationContext
                        )

                        downloadNotify(
                            imageFilename,
                            downloadId,
                            Notifier.STATE_COMPLETE,
                        )
                    }

                    DownloadData.IS_VIDEO, DownloadData.IS_VIDEO or DownloadData.IS_HLS -> {
                        val isHLS =
                            downloadData.flags == DownloadData.IS_VIDEO or DownloadData.IS_HLS

                        val playlist =
                            if (isHLS)
                                getHLSPlaylist(downloadData.mediaUrl)
                            else
                                HLSUri(downloadData.mediaUrl, "")

                        val videoFileUri = applicationContext.createFile(
                            downloadData.filename,
                            contentUri,
                            "video/mp4"
                        )
                        var audioFileUri: Uri? = null

                        if (videoFileUri == null)
                            throw FileNotFoundException("Failed to create or find file. Ensure the URI supplied is a directory.")

                        val videoFilename = videoFileUri.getFilename()

                        if (isHLS) {
                            audioFileUri = applicationContext.createFile(
                                downloadData.filename,
                                contentUri,
                                "audio/aac"
                            )

                            if (audioFileUri == null)
                                throw FileNotFoundException("Failed to create or find file. Ensure the URI supplied is a directory.")
                        }

                        downloadNotify(
                            videoFilename,
                            downloadId,
                            Notifier.STATE_DOWNLOADING_VIDEO,
                        )

                        downloadVideo(
                            videoUrl = playlist.video,
                            uri = videoFileUri,
                            context = applicationContext,
                            isHLS = isHLS
                        )

                        if (playlist.audio.isNotEmpty()) {
                            downloadNotify(
                                videoFilename,
                                downloadId,
                                Notifier.STATE_DOWNLOADING_AUDIO
                            )

                            downloadAudio(
                                audioUrl = playlist.audio,
                                uri = audioFileUri!!,
                                context = applicationContext,
                            )
                        }

                        downloadNotify(
                            videoFilename,
                            downloadId,
                            Notifier.STATE_COMPLETE
                        )
                    }
                }

                downloadQueue[downloadId] = false
                stopServiceIfDone()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun getHLSPlaylist(url: String): HLSUri =
        withContext(ioDispatcher) {
            val client = okHttpCallFactory.get()

            // Get HLS playlist
            val playlistRequest = Request.Builder().url(url).build()
            val playlist: MutableList<String> = mutableListOf()

            client.newCall(playlistRequest).execute().use { response ->
                val body = requireNotNull(response.body())
                playlist.addAll(body.byteStream().readAllLines())
            }

            // Parse playlist to get audio URI
            val uris = HLSParser.parsePlaylist(playlist)
            val parentPath = url.substring(0, url.lastIndexOf('/') + 1)

            return@withContext HLSUri(
                video = "$parentPath${uris.video}",
                audio = "$parentPath${uris.audio}"
            )
        }

    // Call this BEFORE downloadAudio
    private suspend fun downloadVideo(
        videoUrl: String,
        uri: Uri,
        context: Context,
        isHLS: Boolean,
    ) = withContext(ioDispatcher) {
        val client = okHttpCallFactory.get()

        // Get the MP4 file from the network
        val mp4Url: String

        if (isHLS) {
            val parentPath = videoUrl
                .substring(0, videoUrl.lastIndexOf('/') + 1)
                .replace("hls", "vid")
            val videoM3U8 = videoUrl.substring(videoUrl.lastIndexOf('/') + 1)
            val mp4File = "${videoM3U8.split("_", ".")[1]}.mp4"

            mp4Url = "$parentPath$mp4File"
        } else {
            mp4Url = videoUrl
        }

        val mp4Request = Request.Builder().url(mp4Url).build()

        client.newCall(mp4Request).execute().use { response ->
            val body = requireNotNull(response.body())

            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                val sink = outputStream.sink().buffer()

                sink.writeAll(body.source())
                sink.flush()
                sink.close()
            }
        }
    }

    private suspend fun downloadAudio(
        audioUrl: String,
        uri: Uri,
        context: Context,
    ) = withContext(ioDispatcher) {
        val client = okHttpCallFactory.get()

        // Get the name of the AAC file
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

            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                val sink = outputStream.sink().buffer()

                sink.writeAll(body.source())
                sink.flush()
                sink.close()
            }
        }
    }

    private suspend fun downloadImage(
        imageUrl: String,
        uri: Uri,
        context: Context
    ) = withContext(ioDispatcher) {
        val client = okHttpCallFactory.get()
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

    private fun downloadNotify(
        filename: String,
        notificationId: Int,
        state: Int
    ) {
        notifier.postDownloadNotification(
            filename = filename,
            notificationId = notificationId,
            state = state
        )
    }

    private fun startService() {
        val downloadSummaryNotification = notifier.postDownloadSummaryNotification()
        startForeground(DOWNLOAD_NOTIFICATION_SUMMARY_ID, downloadSummaryNotification)
    }

    private fun stopServiceIfDone() {
        if (downloadQueue.valueIterator().asSequence().none { it }) {
            ServiceCompat.stopForeground(
                this@KiteDownloadService,
                ServiceCompat.STOP_FOREGROUND_DETACH
            )
            stopSelf()
        }
    }
}

private fun Context.createFile(
    filename: String,
    directoryUri: Uri,
    mimeType: String
): Uri? {
    // Get the URI of the directory to write to
    val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
        directoryUri,
        DocumentsContract.getTreeDocumentId(directoryUri)
    )

    // Create the AAC file (empty)
    val fileUri = DocumentsContract.createDocument(
        this.contentResolver,
        folderUri,
        mimeType,
        filename
    )

    return fileUri
}

private fun Uri.getFilename() = this.toString()
    .substringAfterLast("%2F")
    .replace("%20", " ")

private const val DOWNLOAD_NOTIFICATION_SUMMARY_ID = 1