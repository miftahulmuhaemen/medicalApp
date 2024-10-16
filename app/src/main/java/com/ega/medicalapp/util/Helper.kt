package com.ega.medicalapp.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}