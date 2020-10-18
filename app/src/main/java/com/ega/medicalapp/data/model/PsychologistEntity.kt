package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class PsychologistEntity(

    var name: String? = "",
    var email: String? = "",
    var photo: String? = "gs://medicalapp-e2fc9.appspot.com/118780058_1806454849507718_4343235856376208408_n.jpg",
    var address: String? = "",
    var experience: String? = "",
    var alumni: String? = "",
    var sipp: String? = "",
    var online: Boolean? = false

) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "photo" to photo,
            "address" to address,
            "experience" to experience,
            "alumni" to alumni,
            "sipp" to sipp,
            "online" to online
        )
    }
}