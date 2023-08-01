package com.hyouteki.oasis.utils

import android.content.Context
import android.content.SharedPreferences

interface Saver {

    companion object {
        private const val TAG = "SharedPreferences@Papyrus"
        private const val MODE = Context.MODE_PRIVATE

        // TAG names
        const val DEFAULT_TAB = "DefaultTab"
        const val CONFIRM_DELETE = "ConfirmDelete"
        const val CONFESSION_ADDRESSED_TO = "ConfessionAddressedTo"
        const val CONFESSION_CONFESSION = "ConfessionConfession"

        fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(TAG, MODE)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(context).edit()
        }
    }
}