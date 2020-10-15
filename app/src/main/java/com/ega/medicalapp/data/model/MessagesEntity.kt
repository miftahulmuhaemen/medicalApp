package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class MessagesEntity(
    var message: String? = "",
    var sender: String? = "",
    var timestamp: Long? = 0
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "message" to message,
            "sender" to sender,
            "timestamp" to timestamp
        )
    }
}