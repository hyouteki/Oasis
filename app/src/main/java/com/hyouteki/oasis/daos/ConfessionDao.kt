package com.hyouteki.oasis.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.oasis.models.Confession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConfessionDao {
    companion object {
        private val database = FirebaseFirestore.getInstance()
        private val collection = database.collection("Confession")

        fun insert(confession: Confession?) {
            confession?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    collection.document(confession.id).set(it)
                }
            }
        }

        fun getCollection() = collection

        fun select(id: String) = collection.document(id).get()
    }
}