package com.hyouteki.oasis.models

data class User(
    val uid: String = "",
    var name: String = "",
    val photoUrl: String = "",
    var bio: String = "Hey fellas; I'm using Oasis",
    val users: ArrayList<String> = arrayListOf(), // chat-mates
    val saved: ArrayList<String> = arrayListOf()  // saved-posts
)
