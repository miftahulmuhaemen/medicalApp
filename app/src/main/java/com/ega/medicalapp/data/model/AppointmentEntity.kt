package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class AppointmentEntity(
    var id: String? = "",
    var patient: String? = "",
    var psychologist: String? = "",
    var status: String? = "",
    var photo: String? = "gs://medicalapp-e2fc9.appspot.com/118780058_1806454849507718_4343235856376208408_n.jpg",
    var patientname: String? = "",
    var time: Int? = 1
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "patient" to patient,
            "psychologist" to psychologist,
            "status" to status,
            "photo" to photo,
            "patientname" to patientname,
            "time" to time
        )
    }
}