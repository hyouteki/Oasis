package com.hyouteki.oasis.models

data class MarketplacePost(
    val userID: String = "",
    var postID: String = "",
    val itemName: String = "",
    val itemDesc: String = "",
    val itemPrice: String = "",
    val categoryTags: ArrayList<String> = arrayListOf(),
    val sellTag: String = "Sell",
    val conditionTag: String = ""
)