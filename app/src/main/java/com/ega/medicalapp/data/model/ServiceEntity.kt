package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class ServiceEntity(
    var address: String? = "",
    var location: String? = "",
    var map: String? = "",
    var name: String? = "",
    var phone: String? = "",
    var time: String? = "",
    var website: String? = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "address" to address,
            "location" to location,
            "map" to map,
            "name" to name,
            "phone" to phone,
            "time" to time,
            "website" to website
        )
    }
}