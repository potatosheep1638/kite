package com.potatosheep.kite.core.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.ServiceCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.potatosheep.kite.core.common.Dispatcher
import com.potatosheep.kite.core.common.KiteDispatchers
import com.potatosheep.kite.core.common.constants.IntentData
import com.potatosheep.kite.core.common.R.string as commonStrings
import com.potatosheep.kite.core.designsystem.R.drawable as KiteDrawable
import com.potatosheep.kite.core.media.model.DownloadData
import com.potatosheep.kite.core.media.util.readAllLines
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
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

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Builder

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

        val client = okHttpCallFactory.get()

        val downloadData: DownloadData? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(IntentData.DOWNLOAD_DATA, DownloadData::class.java)
            } else {
                intent?.getParcelableExtra(IntentData.DOWNLOAD_DATA)
            }

        if (downloadData == null) {
            throw IllegalArgumentException("DownloadData cannot be null")
        } else {
            lifecycleScope.launch {
                when (downloadData.flags) {
                    DownloadData.IS_IMAGE -> {
                        val filename = downloadData.contentUri.substringAfterLast("%2F")

                        updateNotification(
                            notificationBuilder,
                            filename,
                            getString(commonStrings.notify_download_image)
                        )

                        downloadImage(
                            client = client,
                            imageUrl = downloadData.mediaUrl,
                            uri = downloadData.contentUri.toUri(),
                            context = applicationContext
                        )
                    }

                    DownloadData.IS_VIDEO, DownloadData.IS_VIDEO or DownloadData.IS_HLS -> {
                        val playlist =
                            if (downloadData.flags == DownloadData.IS_VIDEO or DownloadData.IS_HLS)
                                getHLSPlaylist(client, downloadData.mediaUrl)
                            else
                                HLSUri(downloadData.mediaUrl, "")

                        val contentUri = downloadData.contentUri.toUri()

                        updateNotification(
                            notificationBuilder,
                            "${downloadData.filename}.mp4",
                            getString(commonStrings.notify_download_video)
                        )

                        downloadVideo(
                            client = client,
                            videoUrl = playlist.video,
                            fileName = downloadData.filename,
                            uri = contentUri,
                            context = applicationContext,
                            isHLS = downloadData.flags == DownloadData.IS_VIDEO or DownloadData.IS_HLS
                        )

                        if (playlist.audio.isNotEmpty()) {
                            updateNotification(
                                notificationBuilder,
                                "",
                                getString(commonStrings.notify_download_audio)
                            )

                            downloadAudio(
                                client = client,
                                audioUrl = playlist.audio,
                                fileName = downloadData.filename,
                                uri = contentUri,
                                context = applicationContext
                            )
                        }
                    }
                }

                updateNotification(
                    notificationBuilder,
                    "",
                    getString(commonStrings.notify_downloaded)
                )
                stopNotification(notificationBuilder)
                stopService()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun getHLSPlaylist(client: Call.Factory, url: String): HLSUri =
        withContext(ioDispatcher) {
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

    private suspend fun downloadVideo(
        client: Call.Factory,
        videoUrl: String,
        fileName: String,
        uri: Uri,
        context: Context,
        isHLS: Boolean
    ) {
        withContext(ioDispatcher) {
            if (fileName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

            // Get the directory URI
            val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
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

                context.contentResolver.openOutputStream(videoFileUri!!)?.let { outputStream ->
                    val sink = outputStream.sink().buffer()

                    sink.writeAll(body.source())
                    sink.flush()
                    sink.close()
                }
            }
        }
    }

    private suspend fun downloadAudio(
        client: Call.Factory,
        audioUrl: String,
        fileName: String,
        uri: Uri,
        context: Context
    ) {
        withContext(ioDispatcher) {
            if (fileName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

            // Get the URI of the directory to write to
            val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )

            // Create the AAC file (empty)
            val audioFileUri = DocumentsContract.createDocument(
                context.contentResolver,
                folderUri,
                "audio/aac",
                fileName
            )

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

                context.contentResolver.openOutputStream(audioFileUri!!)?.let { outputStream ->
                    val sink = outputStream.sink().buffer()

                    sink.writeAll(body.source())
                    sink.flush()
                    sink.close()
                }
            }
        }
    }

    private suspend fun downloadImage(
        client: Call.Factory,
        imageUrl: String,
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

    // TODO: Move notification logic to :core:notifications
    private fun startService() {
        notificationManager = getSystemService()!!

        val summaryChannel = NotificationChannel(
            DOWNLOAD_CHANNEL,
            getString(commonStrings.download),
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(summaryChannel)

        notificationBuilder = Builder(this, DOWNLOAD_CHANNEL)
            .setSmallIcon(KiteDrawable.round_file_download)
            .setContentTitle(getString(commonStrings.notify_initial_title)) // Temp name; set later
            .setContentText(getString(commonStrings.notify_prepare))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setGroup(DOWNLOAD_NOTIFICATION_GROUP)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setGroupSummary(true)

        startForeground(DOWNLOAD_NOTIFICATION_SUMMARY_ID, notificationBuilder.build())
    }

    private fun updateNotification(
        notificationBuilder: Builder,
        title: String = "",
        text: String = ""
    ) {
        if (title.isNotEmpty()) notificationBuilder.setContentTitle(title)
        if (text.isNotEmpty()) notificationBuilder.setContentText(text)

        notificationManager.notify(DOWNLOAD_NOTIFICATION_SUMMARY_ID, notificationBuilder.build())
    }

    private fun stopNotification(notificationBuilder: Builder) {
        notificationBuilder
            .setOngoing(false)
            .clearActions()

        notificationManager.notify(DOWNLOAD_NOTIFICATION_SUMMARY_ID, notificationBuilder.build())
    }

    private fun stopService() {
        ServiceCompat.stopForeground(this@KiteDownloadService, ServiceCompat.STOP_FOREGROUND_DETACH)
        stopSelf()
    }
}

private const val DOWNLOAD_NOTIFICATION_SUMMARY_ID = 1
private const val DOWNLOAD_CHANNEL = "download_channel"
private const val DOWNLOAD_NOTIFICATION_GROUP = "download_group"
