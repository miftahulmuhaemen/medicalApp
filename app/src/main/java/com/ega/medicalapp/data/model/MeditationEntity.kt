package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class MeditationEntity(
    var title: String? = "",
    var description: String? = "",
    var photo: String? = "",
    var url: String? = ""
) : Parcelable