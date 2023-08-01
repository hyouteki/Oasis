package com.hyouteki.oasis.classes

import androidx.fragment.app.Fragment

open class CustomFragment(private val title: String, private val action: Int) : Fragment() {

    companion object {
        const val NO_ACTION = 0
        const val ADD_ACTION = 1
        const val SEARCH_ACTION = 2
        const val ADD_SEARCH_ACTION = 3
        const val REFRESH_ACTION = 4
        const val REFRESH_SEARCH_ACTION = 7
        const val EDIT_ACTION = 5
    }

    fun getTitle(): String {
        return this.title
    }

    fun getAction(): Int {
        return this.action
    }

}