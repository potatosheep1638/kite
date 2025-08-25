package com.potatosheep.kite.core.notification

interface Notifier {
    fun postDownloadNotification(
        filename: String,
        notificationId: Int
    )

    fun updateDownloadNotification(
        notificationId: Int,
        state: Int
    )
}