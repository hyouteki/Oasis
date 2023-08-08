package com.hyouteki.oasis.viewmodels

import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.daos.UserDao
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User

interface OasisViewModel {
    companion object {
        // dao objects
        private val userDAO = UserDao()
        val postDAO = PostDao()

        // User based API calls
        fun addUser(user: User) = userDAO.addUser(user)
        fun getUserDocument(id: String) = userDAO.getUserDocument(id)
        fun addUserIfNotPresent(user: User?) = userDAO.addIfNotPresent(user)
        fun addToSavedUsersIfNotPresent(id1: String, id2: String) {
            userDAO.addToSavedUserIfNotPresent(id1, id2)
            userDAO.addToSavedUserIfNotPresent(id2, id1)
        }

        // Post based API calls
        fun addMarketplacePost(marketplacePost: MarketplacePost) =
            postDAO.addMarketplacePost(marketplacePost)
        fun updateMarketplacePost(marketplacePost: MarketplacePost) =
            postDAO.updateMarketplacePost(marketplacePost)


//        val chatCollection = ChatDao.collections
//        fun addChat(chat: Chat) = ChatDao.addChat(chat)
//        fun addConfession(confession: Confession?) = ConfessionDao.insert(confession)
//        fun selectConfession(id: String) = ConfessionDao.select(id)
    }
}