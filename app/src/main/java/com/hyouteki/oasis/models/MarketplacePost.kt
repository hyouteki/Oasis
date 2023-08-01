package com.hyouteki.oasis.models

data class MarketplacePost(
    val uid: String = "",
    var pid: String = "",
    val imageUrl1: String = "null",
    val imageUrl2: String = "null",
    val imageUrl3: String = "null",
    val imageUrl4: String = "null",
    val imageUrl5: String = "null",
    val itemName: String = "",
    val itemDesc: String = "",
    val itemPrice: String = "",
    val itemType: Boolean = PRODUCT,
    val postType: String = "Sell",
    val lendingTime: String = "",
    val tag1: String = "null",
    val tag2: String = "null",
    val tag3: String = "null"
) {
    companion object {
        const val PRODUCT = true
        const val EXPERIENCE = false
        const val SELL = "Sell"
        const val LEND = "Lend"
        const val LOST = "Lost"
        const val FOUND = "Found"
    }
}