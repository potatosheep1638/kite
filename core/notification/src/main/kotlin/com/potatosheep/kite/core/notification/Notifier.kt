package com.potatosheep.kite.core.notification

import android.app.Notification

/**
 * Interface for classes that create notifications for Kite.
 */
interface Notifier {
    /**
     * Post a download summary notification.
     *
     * @return the download summary [Notification]
     */
    fun postDownloadSummaryNotification(): Notification

    /**
     * Post a download notification.
     *
     * @param filename the name of the file to be downloaded
     * @param downloadId the ID of the download
     * @param notificationId the ID of the notification
     * @param state the initial state of the target download notification, such as
     * [Notifier.STATE_DOWNLOADING_IMAGE]
     *
     * @return the download [Notification]
     */
    fun postDownloadNotification(
        filename: String,
        downloadId: Int,
        notificationId: Int,
        state: Int
    ): Notification

    companion object {
        /**
         * State for when image download is in progress.
         */
        const val STATE_DOWNLOADING_IMAGE = 1
        /**
         * State for when video download is in progress.
         */
        const val STATE_DOWNLOADING_VIDEO = 2
        /**
         * State for when audio download is in progress.
         */
        const val STATE_DOWNLOADING_AUDIO = 4
        /**
         * State for when a download is complete.
         */
        const val STATE_COMPLETE = 5
        /**
         * State for when a download is cancelled.
         */
        const val STATE_STOPPED = 6
    }
}