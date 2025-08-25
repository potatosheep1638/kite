package com.potatosheep.kite.core.notification

/**
 * Interface for classes that create notifications for Kite
 */
interface Notifier {
    /**
     * Post a download notification
     *
     * @param filename the name of the file to be downloaded
     * @param notificationId the ID of the notification
     */
    fun postDownloadNotification(
        filename: String,
        notificationId: Int
    )

    /**
     * Update a download notification
     *
     * @param notificationId the ID of the target download notification
     * @param state the desired state of the target download notification
     */
    fun updateDownloadNotification(
        notificationId: Int,
        state: Int
    )
}