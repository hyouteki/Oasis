package com.hyouteki.oasis.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.R

interface Helper {
    companion object {
        val user = FirebaseAuth.getInstance().currentUser
        fun makeToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun formatTime(time: String): String {
            return "${time.subSequence(0, 10)} ${time.subSequence(11, 16)}"
        }

        fun makeSnackBar(view: View, message: String, resources: Resources) {
            Snackbar
                .make(view, message, Snackbar.LENGTH_SHORT)
                .setAnchorView(view)
                .setBackgroundTint(resources.getColor(R.color.colorQuaternary))
                .setTextColor(resources.getColor(R.color.colorTertiary))
                .show()
        }
    }
}