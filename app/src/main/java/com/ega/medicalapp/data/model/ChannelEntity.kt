package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class ChannelEntity(
    var lastMessage: String? = "",
    var endSession: Boolean? = false,
    var patient: String? = "",
    var psychologist: String? = "",
    var timestamp: Long? = 0,
    var patientname: String? = "",
    var patientphoto: String? = "",
    var id: String? = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "lastMessage" to lastMessage,
            "endSession" to endSession,
            "patient" to patient,
            "psychologist" to psychologist,
            "timestamp" to timestamp,
            "patientname" to patientname,
            "patientphoto" to patientphoto,
            "id" to id
        )
    }
}