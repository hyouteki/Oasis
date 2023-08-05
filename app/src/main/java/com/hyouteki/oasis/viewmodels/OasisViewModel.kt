package com.hyouteki.oasis.viewmodels

import com.hyouteki.oasis.daos.UserDao
import com.hyouteki.oasis.models.User

interface OasisViewModel {
    companion object {
        fun addUser(user: User) = UserDao.addUser(user)
        fun getUserDocument(id: String) = UserDao.getUserDocument(id)
        fun addUserIfNotPresent(user: User?) = UserDao.addIfNotPresent(user)
        fun addToSavedUsersIfNotPresent(id1: String, id2: String) {
            UserDao.addToSavedUserIfNotPresent(id1, id2)
            UserDao.addToSavedUserIfNotPresent(id2, id1)
        }
//        val chatCollection = ChatDao.collections
//        fun addChat(chat: Chat) = ChatDao.addChat(chat)
//        fun addConfession(confession: Confession?) = ConfessionDao.insert(confession)
//        fun selectConfession(id: String) = ConfessionDao.select(id)
    }
}