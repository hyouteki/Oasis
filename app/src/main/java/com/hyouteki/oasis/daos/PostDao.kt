package com.hyouteki.oasis.daos

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.oasis.models.MarketplacePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostDao {
    private val database = FirebaseFirestore.getInstance()
    private val marketplacePostCollection = database.collection("MarketplacePost")

    companion object {
        const val TAG = "POST_DAO"
    }

    fun addMarketplacePost(marketplacePost: MarketplacePost?) {
        marketplacePost?.let {
            GlobalScope.launch(Dispatchers.IO) {
                marketplacePostCollection.document(marketplacePost.postID).set(it)
            }
        }
    }

    fun updateMarketplacePost(marketplacePost: MarketplacePost?) {
        marketplacePostCollection.document(marketplacePost?.postID!!).set(marketplacePost)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }
    }
}