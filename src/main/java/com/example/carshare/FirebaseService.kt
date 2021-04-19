package com.example.carshare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("User")
        val updateValues: MutableMap<String, Any> = HashMap()
        updateValues["notificationToken"] = newToken
        if (user != null) {
            ref.child(user.uid).updateChildren(updateValues)
                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        Log.d("FirebaseService", "New Notification Token Generated")
                    }
                }).addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                    }
                })
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent : Intent

        if (message.data["notificationType"] == "User Requests Destination") {
            intent = Intent(this, MapCarOwnerActivity::class.java).apply {

                putExtra("userId", message.data["userId"])
                putExtra("carOwnerId", message.data["carOwnerId"])
                putExtra("username", message.data["username"])
                putExtra("carOwnerName", message.data["carOwnerName"])
                putExtra("userPhone", message.data["userPhone"])
                putExtra("carOwnerPhone", message.data["carOwnerPhone"])
                putExtra("userToken", message.data["userToken"])
                putExtra("carOwnerToken", message.data["carOwnerToken"])
                putExtra("imageLink", message.data["imageLink"])
                putExtra("vehiclePlate", message.data["vehiclePlate"])
                putExtra("numOfDays", message.data["numOfDays"])
                putExtra("totalPayment", message.data["totalPayment"])
                putExtra("notificationType", message.data["notificationType"])
                putExtra("carKey", message.data["carKey"])
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)

        } else if (message.data["notificationType"] == "Car Owner Rejects Offer") {
            intent = Intent(this, MainPageActivity::class.java).apply {

                putExtra("userId", message.data["userId"])
                putExtra("carOwnerId", message.data["carOwnerId"])
                putExtra("username", message.data["username"])
                putExtra("carOwnerName", message.data["carOwnerName"])
                putExtra("userPhone", message.data["userPhone"])
                putExtra("carOwnerPhone", message.data["carOwnerPhone"])
                putExtra("userToken", message.data["userToken"])
                putExtra("carOwnerToken", message.data["carOwnerToken"])
                putExtra("imageLink", message.data["imageLink"])
                putExtra("vehiclePlate", message.data["vehiclePlate"])
                putExtra("numOfDays", message.data["numOfDays"])
                putExtra("totalPayment", message.data["totalPayment"])
                putExtra("notificationType", message.data["notificationType"])
                putExtra("carKey", message.data["carKey"])
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)

        } else if (message.data["notificationType"] == "Car Owner Accepts Offer") {
            intent = Intent(this, PaymentActivity::class.java).apply {

                putExtra("userId", message.data["userId"])
                putExtra("carOwnerId", message.data["carOwnerId"])
                putExtra("username", message.data["username"])
                putExtra("carOwnerName", message.data["carOwnerName"])
                putExtra("userPhone", message.data["userPhone"])
                putExtra("carOwnerPhone", message.data["carOwnerPhone"])
                putExtra("userToken", message.data["userToken"])
                putExtra("carOwnerToken", message.data["carOwnerToken"])
                putExtra("imageLink", message.data["imageLink"])
                putExtra("vehiclePlate", message.data["vehiclePlate"])
                putExtra("numOfDays", message.data["numOfDays"])
                putExtra("totalPayment", message.data["totalPayment"])
                putExtra("notificationType", message.data["notificationType"])
                putExtra("carKey", message.data["carKey"])
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        } else if (message.data["notificationType"] == "User Completes Rental Payment") {
            intent = Intent(this, MapCarOwnerActivity::class.java).apply {

                putExtra("userId", message.data["userId"])
                putExtra("carOwnerId", message.data["carOwnerId"])
                putExtra("username", message.data["username"])
                putExtra("carOwnerName", message.data["carOwnerName"])
                putExtra("userPhone", message.data["userPhone"])
                putExtra("carOwnerPhone", message.data["carOwnerPhone"])
                putExtra("userToken", message.data["userToken"])
                putExtra("carOwnerToken", message.data["carOwnerToken"])
                putExtra("imageLink", message.data["imageLink"])
                putExtra("vehiclePlate", message.data["vehiclePlate"])
                putExtra("numOfDays", message.data["numOfDays"])
                putExtra("totalPayment", message.data["totalPayment"])
                putExtra("notificationType", message.data["notificationType"])
                putExtra("carKey", message.data["carKey"])
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        } else if (message.data["notificationType"] == "Car Owner Arrives") {
            intent = Intent(this, MainPageActivity::class.java).apply {

                putExtra("userId", message.data["userId"])
                putExtra("carOwnerId", message.data["carOwnerId"])
                putExtra("username", message.data["username"])
                putExtra("carOwnerName", message.data["carOwnerName"])
                putExtra("userPhone", message.data["userPhone"])
                putExtra("carOwnerPhone", message.data["carOwnerPhone"])
                putExtra("userToken", message.data["userToken"])
                putExtra("carOwnerToken", message.data["carOwnerToken"])
                putExtra("imageLink", message.data["imageLink"])
                putExtra("vehiclePlate", message.data["vehiclePlate"])
                putExtra("numOfDays", message.data["numOfDays"])
                putExtra("totalPayment", message.data["totalPayment"])
                putExtra("notificationType", message.data["notificationType"])
                putExtra("carKey", message.data["carKey"])
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_car)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}