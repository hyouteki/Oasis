package com.hyouteki.oasis.comms

import com.hyouteki.oasis.models.Confession

interface AdapterComms {
    fun onConfessionLongClicked(model: Confession) {}
}