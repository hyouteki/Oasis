package com.hyouteki.oasis.classes

import com.hyouteki.oasis.models.Chat
import com.hyouteki.oasis.models.MarketplacePost

interface Communicator {
    fun launchAddMarketplacePostActivity() {}
    fun launchAddLostAndFoundPostActivity() {}
    fun launchAddClubEventPostActivity() {}
    fun launchFullImageDialog(marketplacePost: MarketplacePost) {}
    fun handleContact(post: MarketplacePost) {}
    fun handlePostAction(post: MarketplacePost) {}
    fun updatePost() {}
    fun deletePost() {}
    fun launchChatActivity(uid: String) {}
    fun onUserClick(uid: String) {}
    fun copyUserId() {}
    fun removeUser() {}
    fun openChatUserOptions(chatUserId: String) {}
    fun copyMessage() {}
    fun deleteMessage() {}
    fun openChatOptions(chat: Chat) {}
}