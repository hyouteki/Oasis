package com.hyouteki.oasis.abstractions

import android.content.Context

interface OasisConstants {
    companion object {
        // FireStore parameters
        const val PAGING_LIMIT = 20

        // shared preferences constants
        const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES"
        const val SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE
    }
}