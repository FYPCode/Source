package com.example.carshare

data class NotificationData(
    val title: String,
    val message: String,
    val userId: String,
    val carOwnerId: String,
    val username: String,
    val carOwnerName: String,
    val userPhone: String,
    val carOwnerPhone: String,
    val userToken: String,
    val carOwnerToken: String,
    val imageLink: String?,
    val vehiclePlate: String?,
    val numOfDays: String?,
    val totalPayment: String?,
    val notificationType: String,
    val carKey: String?
)