package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
data class Article(
    var date: String? = "",
    var description: String? = "",
    var title: String? = "",
    var url: String? = ""
)