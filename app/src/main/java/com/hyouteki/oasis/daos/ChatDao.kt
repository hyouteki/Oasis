package com.hyouteki.oasis.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.oasis.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatDao {
    companion object {
        private val database = FirebaseFirestore.getInstance()
        val collection = database.collection("Chat")

        fun addChat(chat: Chat?) {
            chat?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    collection.document(chat.id).set(it)
                }
            }
        }
    }
}