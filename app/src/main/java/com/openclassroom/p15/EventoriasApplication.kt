package com.openclassroom.p15

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.openclassroom.p15.service.EventoriasFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EventoriasApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerFcmToken()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EventoriasFirebaseMessagingService.CHANNEL_ID,
                "Événements Eventorias",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications pour les nouveaux événements"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun registerFcmToken() {
        // Use addAuthStateListener so we wait for Firebase Auth to restore the session
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val uid = auth.currentUser?.uid ?: return@addAuthStateListener
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}
