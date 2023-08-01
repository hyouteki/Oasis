package com.hyouteki.oasis.utils

import android.util.Log

interface Logger {
    companion object {
        private const val TAG = "Logger@Oasis"
        fun debugger(message: String) {
            Log.d(TAG, message)
        }

        fun warning(message: String) {
            Log.w(TAG, message)
        }

        fun warning(message: String, exception: Exception) {
            Log.w(TAG, message, exception)
        }

        fun info(message: String) {
            Log.i(TAG, message)
        }
    }
}