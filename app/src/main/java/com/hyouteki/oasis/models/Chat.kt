package com.hyouteki.oasis.models

// sender = false; user1 = sender
data class Chat(
    val id: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val message: String = "",
    val sender: Boolean = false,
    val time: String = ""
)
