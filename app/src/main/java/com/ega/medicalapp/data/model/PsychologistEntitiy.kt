package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class PsychologistEntitiy(
    var name: String? = "",
    var email: String? = "",
    var photo: String? = "",
    var age: Int? = 0,
    var isOnline: Boolean? = false
) : Parcelable