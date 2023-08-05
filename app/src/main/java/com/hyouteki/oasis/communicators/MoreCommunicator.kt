package com.hyouteki.oasis.communicators

interface MoreCommunicator {
    fun handlePostAddAction()
    fun handleUserIDCopyAction()
    fun handleProfileEditAction()
    fun handleSavedAction()
    fun handleInviteAction()
    fun handleSettingsAction()
    fun handleSignOutAction()
    fun handleCopyrightAction()
}