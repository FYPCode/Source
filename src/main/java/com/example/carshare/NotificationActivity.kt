package com.example.carshare

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val TOPIC = "/topics/myTopic2"

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val notificationRef = FirebaseDatabase.getInstance().getReference("Notification")
        val data = intent
        val userId = data.getStringExtra("userId")
        val carOwnerId = data.getStringExtra("carOwnerId")
        val username = data.getStringExtra("username")
        val carOwnerName = data.getStringExtra("carOwnerName")
        val userPhone = data.getStringExtra("userPhone");
        val carOwnerPhone = data.getStringExtra("carOwnerPhone");
        val userToken = data.getStringExtra("userToken")
        val carOwnerToken = data.getStringExtra("carOwnerToken")
        val imageLink = data.getStringExtra("imageLink")
        val vehiclePlate = data.getStringExtra("vehiclePlate")
        val numOfDays = data.getStringExtra("numOfDays")
        val totalPayment = data.getStringExtra("totalPayment")
        val notificationType = data.getStringExtra("notificationType")
        val carKey = data.getStringExtra("carKey")

        pushNotificationType(
            notificationType,
            userId,
            carOwnerId,
            username,
            carOwnerName,
            userPhone,
            carOwnerPhone,
            userToken,
            carOwnerToken,
            notificationRef,
            imageLink,
            vehiclePlate,
            numOfDays,
            totalPayment,
            carKey
        )
    }

    private fun pushNotificationType(
        notificationType: String?,
        userId: String,
        carOwnerId: String,
        username: String,
        carOwnerName: String,
        userPhone: String,
        carOwnerPhone: String,
        userToken: String,
        carOwnerToken: String,
        notificationRef: DatabaseReference,
        imageLink: String?,
        vehiclePlate: String?,
        numOfDays: String?,
        totalPayment: String?,
        carKey: String?
    ) {
        if (notificationType == "User Requests Destination") {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            notificationRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var notificationCount = dataSnapshot.childrenCount
                        notificationCount++;
                        val notificationId = "Notification$notificationCount"
                        val updateValues: MutableMap<String, Any> =
                            HashMap()
                        updateValues["userId"] =
                            userId
                        updateValues["carOwnerId"] =
                            carOwnerId
                        updateValues["userToken"] =
                            userToken
                        updateValues["carOwnerToken"] =
                            carOwnerToken
                        updateValues["notificationType"] =
                            notificationType
                        notificationRef.child(
                            notificationId
                        )
                            .updateChildren(
                                updateValues
                            )
                            .addOnSuccessListener(
                                object :
                                    OnSuccessListener<Void?> {
                                    override fun onSuccess(
                                        aVoid: Void?
                                    ) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                        val title = "A customer wants to rent your car"
                                        val message =
                                            "$username wants to rent your car $vehiclePlate for $numOfDays days, paying you $$totalPayment in total, will you accept the offer?"
                                        etTitle.setText(
                                            title
                                        )
                                        etMessage.setText(
                                            message
                                        )
                                        etPhone.setText(
                                            userPhone
                                        )
                                        etPhone2.setText(
                                            carOwnerPhone
                                        )
                                        val recipientToken = carOwnerToken
                                        if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                            PushNotification(
                                                NotificationData(
                                                    title,
                                                    message,
                                                    userId,
                                                    carOwnerId,
                                                    username,
                                                    carOwnerName,
                                                    userPhone,
                                                    carOwnerPhone,
                                                    userToken,
                                                    carOwnerToken,
                                                    imageLink,
                                                    vehiclePlate,
                                                    numOfDays,
                                                    totalPayment,
                                                    notificationType,
                                                    carKey
                                                ), recipientToken
                                            ).also {
                                                sendNotification(it)
                                                Toast.makeText(
                                                    this@NotificationActivity,
                                                    "Notification message has been sent.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(
                                object :
                                    OnFailureListener {
                                    override fun onFailure(
                                        e: Exception
                                    ) {
                                        Toast.makeText(
                                            this@NotificationActivity,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
        } else if (notificationType == "Car Owner Rejects Offer") {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            notificationRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var notificationCount = dataSnapshot.childrenCount
                        notificationCount++;
                        val notificationId = "Notification$notificationCount"
                        val updateValues: MutableMap<String, Any> =
                            HashMap()
                        updateValues["userId"] =
                            userId
                        updateValues["carOwnerId"] =
                            carOwnerId
                        updateValues["userToken"] =
                            userToken
                        updateValues["carOwnerToken"] =
                            carOwnerToken
                        updateValues["notificationType"] =
                            notificationType
                        notificationRef.child(
                            notificationId
                        )
                            .updateChildren(
                                updateValues
                            )
                            .addOnSuccessListener(
                                object :
                                    OnSuccessListener<Void?> {
                                    override fun onSuccess(
                                        aVoid: Void?
                                    ) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                        val title = "The car owner rejects your offer"
                                        val message =
                                            "$carOwnerName rejects your offer of renting the car $vehiclePlate."
                                        etTitle.setText(
                                            title
                                        )
                                        etMessage.setText(
                                            message
                                        )
                                        etPhone.setText(
                                            userPhone
                                        )
                                        etPhone2.setText(
                                            carOwnerPhone
                                        )
                                        val recipientToken = userToken
                                        if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                            PushNotification(
                                                NotificationData(
                                                    title,
                                                    message,
                                                    userId,
                                                    carOwnerId,
                                                    username,
                                                    carOwnerName,
                                                    userPhone,
                                                    carOwnerPhone,
                                                    userToken,
                                                    carOwnerToken,
                                                    imageLink,
                                                    vehiclePlate,
                                                    numOfDays,
                                                    totalPayment,
                                                    notificationType,
                                                    carKey
                                                ), recipientToken
                                            ).also {
                                                sendNotification(it)
                                                Toast.makeText(
                                                    this@NotificationActivity,
                                                    "Notification message has been sent.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(
                                object :
                                    OnFailureListener {
                                    override fun onFailure(
                                        e: Exception
                                    ) {
                                        Toast.makeText(
                                            this@NotificationActivity,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
        } else if (notificationType == "Car Owner Accepts Offer") {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            notificationRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var notificationCount = dataSnapshot.childrenCount
                        notificationCount++;
                        val notificationId = "Notification$notificationCount"
                        val updateValues: MutableMap<String, Any> =
                            HashMap()
                        updateValues["userId"] =
                            userId
                        updateValues["carOwnerId"] =
                            carOwnerId
                        updateValues["userToken"] =
                            userToken
                        updateValues["carOwnerToken"] =
                            carOwnerToken
                        updateValues["notificationType"] =
                            notificationType
                        notificationRef.child(
                            notificationId
                        )
                            .updateChildren(
                                updateValues
                            )
                            .addOnSuccessListener(
                                object :
                                    OnSuccessListener<Void?> {
                                    override fun onSuccess(
                                        aVoid: Void?
                                    ) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                        val title = "The car owner accepts your offer"
                                        val message =
                                            "$carOwnerName accepts your offer of renting the car $vehiclePlate, please proceed to pay."
                                        etTitle.setText(
                                            title
                                        )
                                        etMessage.setText(
                                            message
                                        )
                                        etPhone.setText(
                                            userPhone
                                        )
                                        etPhone2.setText(
                                            carOwnerPhone
                                        )
                                        val recipientToken = userToken
                                        if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                            PushNotification(
                                                NotificationData(
                                                    title,
                                                    message,
                                                    userId,
                                                    carOwnerId,
                                                    username,
                                                    carOwnerName,
                                                    userPhone,
                                                    carOwnerPhone,
                                                    userToken,
                                                    carOwnerToken,
                                                    imageLink,
                                                    vehiclePlate,
                                                    numOfDays,
                                                    totalPayment,
                                                    notificationType,
                                                    carKey
                                                ), recipientToken
                                            ).also {
                                                sendNotification(it)
                                                Toast.makeText(
                                                    this@NotificationActivity,
                                                    "Notification message has been sent.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(
                                object :
                                    OnFailureListener {
                                    override fun onFailure(
                                        e: Exception
                                    ) {
                                        Toast.makeText(
                                            this@NotificationActivity,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
        } else if (notificationType == "User Completes Rental Payment") {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            notificationRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var notificationCount = dataSnapshot.childrenCount
                        notificationCount++;
                        val notificationId = "Notification$notificationCount"
                        val updateValues: MutableMap<String, Any> =
                            HashMap()
                        updateValues["userId"] =
                            userId
                        updateValues["carOwnerId"] =
                            carOwnerId
                        updateValues["userToken"] =
                            userToken
                        updateValues["carOwnerToken"] =
                            carOwnerToken
                        updateValues["notificationType"] =
                            notificationType
                        notificationRef.child(
                            notificationId
                        )
                            .updateChildren(
                                updateValues
                            )
                            .addOnSuccessListener(
                                object :
                                    OnSuccessListener<Void?> {
                                    override fun onSuccess(
                                        aVoid: Void?
                                    ) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                        val title = "The customer has paid the rental payment"
                                        val message =
                                            "$username has paid $$totalPayment to rent your car $vehiclePlate for $numOfDays days. Please deliver to the destination"
                                        etTitle.setText(
                                            title
                                        )
                                        etMessage.setText(
                                            message
                                        )
                                        etPhone.setText(
                                            userPhone
                                        )
                                        etPhone2.setText(
                                            carOwnerPhone
                                        )
                                        val recipientToken = carOwnerToken
                                        if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                            PushNotification(
                                                NotificationData(
                                                    title,
                                                    message,
                                                    userId,
                                                    carOwnerId,
                                                    username,
                                                    carOwnerName,
                                                    userPhone,
                                                    carOwnerPhone,
                                                    userToken,
                                                    carOwnerToken,
                                                    imageLink,
                                                    vehiclePlate,
                                                    numOfDays,
                                                    totalPayment,
                                                    notificationType,
                                                    carKey
                                                ), recipientToken
                                            ).also {
                                                sendNotification(it)
                                                Toast.makeText(
                                                    this@NotificationActivity,
                                                    "Notification message has been sent.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(
                                object :
                                    OnFailureListener {
                                    override fun onFailure(
                                        e: Exception
                                    ) {
                                        Toast.makeText(
                                            this@NotificationActivity,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
        } else if (notificationType == "Car Owner Arrives") {
            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            notificationRef.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var notificationCount = dataSnapshot.childrenCount
                        notificationCount++;
                        val notificationId = "Notification$notificationCount"
                        val updateValues: MutableMap<String, Any> =
                            HashMap()
                        updateValues["userId"] =
                            userId
                        updateValues["carOwnerId"] =
                            carOwnerId
                        updateValues["userToken"] =
                            userToken
                        updateValues["carOwnerToken"] =
                            carOwnerToken
                        updateValues["notificationType"] =
                            notificationType
                        notificationRef.child(
                            notificationId
                        )
                            .updateChildren(
                                updateValues
                            )
                            .addOnSuccessListener(
                                object :
                                    OnSuccessListener<Void?> {
                                    override fun onSuccess(
                                        aVoid: Void?
                                    ) {
                                        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                        val title = "The car owner arrives at the destination"
                                        val message =
                                            "$carOwnerName has arrived at the destination. The vehicle plate is $vehiclePlate"
                                        etTitle.setText(
                                            title
                                        )
                                        etMessage.setText(
                                            message
                                        )
                                        etPhone.setText(
                                            userPhone
                                        )
                                        etPhone2.setText(
                                            carOwnerPhone
                                        )
                                        val recipientToken = userToken
                                        if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                            PushNotification(
                                                NotificationData(
                                                    title,
                                                    message,
                                                    userId,
                                                    carOwnerId,
                                                    username,
                                                    carOwnerName,
                                                    userPhone,
                                                    carOwnerPhone,
                                                    userToken,
                                                    carOwnerToken,
                                                    imageLink,
                                                    vehiclePlate,
                                                    numOfDays,
                                                    totalPayment,
                                                    notificationType,
                                                    carKey
                                                ), recipientToken
                                            ).also {
                                                sendNotification(it)
                                                Toast.makeText(
                                                    this@NotificationActivity,
                                                    "Notification message has been sent.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                })
                            .addOnFailureListener(
                                object :
                                    OnFailureListener {
                                    override fun onFailure(
                                        e: Exception
                                    ) {
                                        Toast.makeText(
                                            this@NotificationActivity,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                }
            })
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("NotificationActivity", "Successful Response: ${Gson().toJson(response)}")
                } else {
                    Log.e(
                        "NotificationActivity",
                        "Failure Response:${response.errorBody().toString()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("NotificationActivity", e.toString())
            }
        }
}