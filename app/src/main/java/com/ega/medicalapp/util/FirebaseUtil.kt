package com.ega.medicalapp.util

import com.google.firebase.auth.FirebaseAuth

fun getFirebaseEmail(): String? {
    return if (FirebaseAuth.getInstance().currentUser?.providerData?.last()?.email.isNullOrEmpty())
        FirebaseAuth.getInstance().currentUser?.uid + "@" + getFirebaseProviderId()
    else
        FirebaseAuth.getInstance().currentUser?.providerData?.last()?.email
}

fun getFirebaseDisplayName(): String? {
    return FirebaseAuth.getInstance().currentUser?.displayName
}

fun getFirebaseProviderId(): String? {
    return FirebaseAuth.getInstance().currentUser?.providerData?.last()?.providerId
}
