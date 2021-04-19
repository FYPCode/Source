package com.example.carshare

data class PushNotification(
    val data: NotificationData,
    val to: String
)