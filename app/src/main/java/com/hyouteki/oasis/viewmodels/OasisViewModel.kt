package com.hyouteki.oasis.viewmodels

import com.hyouteki.oasis.daos.ChatDao
import com.hyouteki.oasis.daos.ConfessionDao
import com.hyouteki.oasis.daos.UserDao
import com.hyouteki.oasis.models.Chat
import com.hyouteki.oasis.models.Confession
import com.hyouteki.oasis.models.User

interface OasisViewModel {
    companion object {
        val chatCollection = ChatDao.collection
        val userCollection = UserDao.collection
        fun addChat(chat: Chat) = ChatDao.addChat(chat)
        fun addUser(user: User) = UserDao.addUser(user)
        fun getUser(id: String) = UserDao.getUser(id)
        fun getUserDocument(id: String) = UserDao.collection.document(id).get()

        fun addUserIfNotPresent(user: User?) = UserDao.addIfNotPresent(user)
        fun addUsersIfNotPresent(id1: String, id2: String) {
            UserDao.addUserIfNotPresent(id1, id2)
            UserDao.addUserIfNotPresent(id2, id1)
        }

        fun addConfession(confession: Confession?) = ConfessionDao.insert(confession)
        fun selectConfession(id: String) = ConfessionDao.select(id)
    }
}