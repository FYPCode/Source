package com.example.carshare;

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.BillingAddressFields
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_rentable_car_details.*
import kotlinx.android.synthetic.main.layout_rent_out.*
import java.util.*
import kotlin.collections.HashMap


var SIGN_IN_CODE = 1

class PaymentActivity : AppCompatActivity() {
    private var currentUser: FirebaseUser? = null
    private lateinit var paymentSession: PaymentSession
    private lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy {
        Stripe(
            applicationContext, PaymentConfiguration.getInstance(
                applicationContext
            ).publishableKey
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val ref = FirebaseDatabase.getInstance().getReference("User")
        val carRef = FirebaseDatabase.getInstance().getReference("Car")
        val paymentRef = FirebaseDatabase.getInstance().getReference("Payment")
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

        loginButton.visibility = View.VISIBLE
        continueRenting.visibility = View.INVISIBLE
        val str = "Please pay $" + totalPayment
        pleasePay.text = str
        pleasePay.visibility = View.VISIBLE
        paymentmethod.visibility = View.INVISIBLE
        payButton.visibility = View.INVISIBLE

        loginButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            //login to firebase
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                SIGN_IN_CODE
            )
        }

        paymentmethod.setOnClickListener {
            // Create the customer session and kick start the payment flow
            paymentSession.presentPaymentMethodSelection()
        }

        payButton.setOnClickListener {
            confirmPayment(
                selectedPaymentMethod.id!!,
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
                carKey,
                ref,
                carRef,
                paymentRef
            )
        }
    }

    private fun confirmPayment(
        paymentMethodId: String,
        userId: String,
        carOwnerId: String,
        username: String,
        carOwnerName: String,
        userPhone: String?,
        carOwnerPhone: String?,
        userToken: String?,
        carOwnerToken: String?,
        imageLink: String,
        vehiclePlate: String,
        numOfDays: String,
        totalPayment: String,
        notificationType: String?,
        carKey: String,
        ref: DatabaseReference,
        carRef: DatabaseReference,
        paymentRef: DatabaseReference
    ) {
        payButton.isEnabled = false
        var payment = totalPayment.toDouble() * 100

        val paymentCollection = Firebase.firestore
            .collection("stripe_customers").document(currentUser?.uid ?: "")
            .collection("payments")

        paymentCollection.add(
            hashMapOf(
                "amount" to payment,
                "currency" to "hkd"
            )
        )
            .addOnSuccessListener { documentReference ->
                documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val clientSecret = snapshot.data?.get("client_secret")

                        clientSecret?.let {
                            stripe.confirmPayment(
                                this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                    paymentMethodId,
                                    (it as String)
                                )
                            )

                            payment /= 100
                            continueRenting.text = ""
                            pleasePay.text = "Thank you for your payment!"
                            Toast.makeText(
                                applicationContext,
                                "Payment Done!",
                                Toast.LENGTH_SHORT
                            ).show()

                            //add payment to car owner's account
                            ref.child(carOwnerId).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val balance = dataSnapshot.child("balance").getValue(
                                            String::class.java
                                        )
                                        val oldBalance = balance?.toDouble()
                                        val newBalance = payment.let { it1 ->
                                            oldBalance?.plus(
                                                it1
                                            )
                                        }
                                        val newBalanceStr = newBalance.toString()

                                        val updateValues: MutableMap<String, Any> = HashMap()
                                        updateValues["balance"] = newBalanceStr
                                        ref.child(carOwnerId).updateChildren(updateValues)
                                            .addOnSuccessListener(
                                                object :
                                                    OnSuccessListener<Void?> {
                                                    override fun onSuccess(aVoid: Void?) {

                                                        val updateValues2: MutableMap<String, Any> =
                                                            HashMap()
                                                        updateValues2["isRentOut"] =
                                                            "Y"
                                                        updateValues2["rentBy"] =
                                                            userId
                                                        carRef.child(carKey)
                                                            .updateChildren(
                                                                updateValues2
                                                            )
                                                            .addOnSuccessListener(
                                                                object :
                                                                    OnSuccessListener<Void?> {
                                                                    override fun onSuccess(
                                                                        aVoid: Void?
                                                                    ) {
                                                                        paymentRef.addListenerForSingleValueEvent(
                                                                            object :
                                                                                ValueEventListener {
                                                                                override fun onDataChange(
                                                                                    dataSnapshot: DataSnapshot
                                                                                ) {
                                                                                    if (dataSnapshot.exists()) {
                                                                                        var paymentCount =
                                                                                            dataSnapshot.childrenCount
                                                                                        paymentCount++;
                                                                                        val paymentId =
                                                                                            "Payment$paymentCount"
                                                                                        val updateValues3: MutableMap<String, Any> =
                                                                                            HashMap()
                                                                                        updateValues3["userId"] =
                                                                                            userId
                                                                                        updateValues3["carOwnerId"] =
                                                                                            carOwnerId
                                                                                        updateValues3["username"] =
                                                                                            username
                                                                                        updateValues3["carOwnerName"] =
                                                                                            carOwnerName
                                                                                        updateValues3["imageLink"] =
                                                                                            imageLink
                                                                                        updateValues3["vehiclePlate"] =
                                                                                            vehiclePlate
                                                                                        updateValues3["numOfDays"] =
                                                                                            numOfDays
                                                                                        updateValues3["totalPayment"] =
                                                                                            totalPayment
                                                                                        updateValues3["customer"] =
                                                                                            snapshot.data!!.getValue(
                                                                                                "customer"
                                                                                            )
                                                                                        updateValues3["id"] =
                                                                                            snapshot.data!!.getValue(
                                                                                                "id"
                                                                                            )
                                                                                        updateValues3["timestamp"] =
                                                                                            snapshot.data!!.getValue(
                                                                                                "created"
                                                                                            )
                                                                                        paymentRef.child(
                                                                                            paymentId
                                                                                        )
                                                                                            .updateChildren(
                                                                                                updateValues3
                                                                                            )
                                                                                            .addOnSuccessListener (
                                                                                                object :
                                                                                                    OnSuccessListener<Void?> {
                                                                                                    override fun onSuccess(
                                                                                                        aVoid: Void?
                                                                                                    ) {
                                                                                                        //send push noti to car owner that the user completes rental payment
                                                                                                        val intent =
                                                                                                            Intent(
                                                                                                                this@PaymentActivity,
                                                                                                                NotificationActivity::class.java
                                                                                                            ).apply {
                                                                                                                putExtra(
                                                                                                                    "userId",
                                                                                                                    userId
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "carOwnerId",
                                                                                                                    carOwnerId
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "username",
                                                                                                                    username
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "carOwnerName",
                                                                                                                    carOwnerName
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "userPhone",
                                                                                                                    userPhone
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "carOwnerPhone",
                                                                                                                    carOwnerPhone
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "userToken",
                                                                                                                    userToken
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "carOwnerToken",
                                                                                                                    carOwnerToken
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "imageLink",
                                                                                                                    imageLink
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "vehiclePlate",
                                                                                                                    vehiclePlate
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "numOfDays",
                                                                                                                    numOfDays
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "totalPayment",
                                                                                                                    totalPayment
                                                                                                                )
                                                                                                                putExtra(
                                                                                                                    "notificationType",
                                                                                                                    "User Completes Rental Payment"
                                                                                                                )
                                                                                                            }
                                                                                                        startActivity(
                                                                                                            intent
                                                                                                        )
                                                                                                        finish()
                                                                                                    }
                                                                                                }
                                                                                                )
                                                                                    }
                                                                                }

                                                                                override fun onCancelled(
                                                                                    error: DatabaseError
                                                                                ) {
                                                                                }
                                                                            })
                                                                    }
                                                                }
                                                            )
                                                    }
                                                })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        }
                    } else {
                        payButton.isEnabled = true
                    }
                }
            }
            .addOnFailureListener { e ->
                payButton.isEnabled = true
            }
    }

    private fun showUI() {
        currentUser?.let {
            loginButton.visibility = View.INVISIBLE

            continueRenting.visibility = View.VISIBLE
            pleasePay.visibility = View.VISIBLE
            payButton.visibility = View.VISIBLE
            paymentmethod.visibility = View.VISIBLE

            setupPaymentSession()
        } ?: run {
            loginButton.visibility = View.VISIBLE

            continueRenting.visibility = View.INVISIBLE
            pleasePay.visibility = View.INVISIBLE
            paymentmethod.visibility = View.INVISIBLE
            payButton.visibility = View.INVISIBLE
            payButton.isEnabled = false
        }
    }

    private fun setupPaymentSession() {
        //Setup Customer Session
        CustomerSession.initCustomerSession(this, FirebaseEphemeralKeyProvider())
        //Setup a payment session
        paymentSession = PaymentSession(
            this, PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .setShippingMethodsRequired(false)
                .setBillingAddressFields(BillingAddressFields.None)
                .setShouldShowGooglePay(false)
                .build()
        )

        paymentSession.init(
            object : PaymentSession.PaymentSessionListener {
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {

                    if (data.isPaymentReadyToCharge) {
                        payButton.isEnabled = true

                        data.paymentMethod?.let {
                            paymentmethod.text =
                                "${it.card?.brand} card ends with ${it.card?.last4}"
                            selectedPaymentMethod = it
                        }
                    }
                }

                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                currentUser = FirebaseAuth.getInstance().currentUser
                showUI()
            } else {
                Toast.makeText(
                    applicationContext,
                    response?.error?.message ?: "Login failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())
        }
    }
}