package com.udacity.politicalpreparedness.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.udacity.politicalpreparedness.BuildConfig
import com.udacity.politicalpreparedness.R

fun Fragment.requirePermissionSnackBar(@StringRes resId: Int) {
    Snackbar.make(requireView(), resId, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.settings) {
            startDetailApplicationSettings(this)
        }.show()
}

private fun startDetailApplicationSettings(context: Fragment) {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    intent.data = uri
    context.startActivity(intent)
}

