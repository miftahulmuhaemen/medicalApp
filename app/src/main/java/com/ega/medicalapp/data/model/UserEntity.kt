package com.ega.medicalapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class UserEntity(
    var name: String? = "",
    var email: String? = "",
    var photo: String? = "",
    var age: Int? = 0,
    var gender: Boolean? = false,
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "photo" to photo,
            "age" to age,
            "gender" to gender
        )
    }
}