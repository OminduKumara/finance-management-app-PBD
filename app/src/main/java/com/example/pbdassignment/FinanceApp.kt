package com.example.pbdassignment

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class FinanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val settings = PersistentCacheSettings.newBuilder().build()
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(settings)
                .build()
    }
}