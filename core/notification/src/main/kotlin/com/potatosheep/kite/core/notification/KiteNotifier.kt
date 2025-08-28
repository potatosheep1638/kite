package com.potatosheep.kite.core.notification

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.potatosheep.kite.core.common.KiteServices
import com.potatosheep.kite.core.common.Service
import com.potatosheep.kite.core.common.constants.DownloadIntent
import com.potatosheep.kite.core.common.constants.IntentData
import com.potatosheep.kite.core.common.R.string as commonStrings
import com.potatosheep.kite.core.designsystem.R.drawable as KiteDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [Notifier]
 */
@Singleton
internal class KiteNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    @Service(KiteServices.Download) private val downloadService: LifecycleService
) : Notifier {

    override fun postDownloadSummaryNotification(): Notification =
        with(context) {
            val summaryNotification = createDownloadNotification {
                val title = getString(commonStrings.download)

                setContentTitle(title)
                    .setContentText(title)
                    .setSmallIcon(KiteDrawable.round_file_download)
                    .setGroup(DOWNLOAD_NOTIFICATION_GROUP)
                    .setGroupSummary(true)
                    .setSilent(true)
                    .setAutoCancel(true)
            }

            return@with summaryNotification
        }

    override fun postDownloadNotification(
        filename: String,
        downloadId: Int,
        notificationId: Int,
        state: Int
    ): Notification =
        with(context) {
            val contentText = getContentText(state)

            val downloadNotification = createDownloadNotification {
                setContentTitle(filename)
                    .setContentText(contentText)
                    .setSmallIcon(
                        if (state == Notifier.STATE_COMPLETE)
                            KiteDrawable.round_check_24
                        else
                            KiteDrawable.round_file_download
                    )
                    .setGroup(DOWNLOAD_NOTIFICATION_GROUP)
                    .setSilent(true)
                    .setAutoCancel(true)

                if (state != Notifier.STATE_COMPLETE && state != Notifier.STATE_STOPPED)
                    addAction(
                        0,
                        getString(commonStrings.cancel),
                        downloadPendingIntent(downloadId, filename)
                    )
            }

            if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                return@with downloadNotification
            }

            val notificationManager = NotificationManagerCompat.from(this)

            notificationManager.notify(
                notificationId,
                downloadNotification
            )

            return@with downloadNotification
        }

    private fun Context.downloadPendingIntent(
        downloadId: Int,
        downloadFilename: String
    ): PendingIntent? =
        PendingIntent.getService(
            this,
            0,
            Intent(this, downloadService::class.java).apply {
                action = DownloadIntent.ACTION_STOP_DOWNLOAD
                putExtra(IntentData.DOWNLOAD_ID, downloadId)
                putExtra(IntentData.DOWNLOAD_FILENAME, downloadFilename)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
}

private fun Context.getContentText(state: Int): String {
    return when (state) {
        Notifier.STATE_DOWNLOADING_IMAGE -> getString(commonStrings.notify_download_image)
        Notifier.STATE_DOWNLOADING_VIDEO -> getString(commonStrings.notify_download_video)
        Notifier.STATE_DOWNLOADING_AUDIO -> getString(commonStrings.notify_download_audio)
        Notifier.STATE_COMPLETE -> getString(commonStrings.notify_downloaded)
        Notifier.STATE_STOPPED -> getString(commonStrings.notify_download_abort)
        else -> getString(commonStrings.error)
    }
}

private fun Context.createDownloadNotification(
    block: NotificationCompat.Builder.() -> Unit
): Notification {
    ensureChannelExists()
    return NotificationCompat.Builder(
        this,
        DOWNLOAD_NOTIFICATION_CHANNEL_ID
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

private fun Context.ensureChannelExists() {
    val channel = NotificationChannel(
        DOWNLOAD_NOTIFICATION_CHANNEL_ID,
        getString(commonStrings.download),
        NotificationManager.IMPORTANCE_DEFAULT
    )

    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "downloadChannel"
private const val DOWNLOAD_NOTIFICATION_GROUP = "downloadGroup"
