package com.hyouteki.oasis.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.oasis.utils.Logger
import com.hyouteki.oasis.models.MarketplacePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostDao {
    private val database = FirebaseFirestore.getInstance()
    val marketplacePostCollection = database.collection("MarketplacePost")

    fun addMarketplacePost(marketplacePost: MarketplacePost?) {
        marketplacePost?.let {
            GlobalScope.launch(Dispatchers.IO) {
                marketplacePostCollection.document(marketplacePost.pid).set(it)
            }
        }
    }

    fun updateMarketplacePost(marketplacePost: MarketplacePost?) {
        marketplacePostCollection.document(marketplacePost?.pid!!)
            .set(marketplacePost)
            .addOnSuccessListener { Logger.debugger("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Logger.warning("Error writing document", e) }
    }
}