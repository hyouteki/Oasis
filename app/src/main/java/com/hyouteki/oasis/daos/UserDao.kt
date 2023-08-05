package com.hyouteki.oasis.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {
    companion object {
        private val database = FirebaseFirestore.getInstance()
        private val collection = database.collection("User")

        fun addUser(user: User?) {
            user?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    collection.document(it.uid).set(it)
                }
            }
        }

        fun getUserDocument(id: String) = collection.document(id).get()

        fun addIfNotPresent(user: User?) {
            user?.let {
                collection.document(it.uid).get().addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java) ?: run {
                        addUser(it)
                    }
                }
            }
        }

        fun addToSavedUserIfNotPresent(id: String, second: String) {
            OasisViewModel.getUserDocument(id).addOnSuccessListener {
                it.toObject(User::class.java)?.let { user ->
                    if (!user.users.contains(second) && id != second) {
                        user.users.add(second)
                        addUser(user)
                    }
                }
            }
        }
    }
}