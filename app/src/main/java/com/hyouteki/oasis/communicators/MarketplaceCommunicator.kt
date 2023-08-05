package com.hyouteki.oasis.communicators

interface MarketplaceCommunicator {
    fun handleMarketplacePostAddAction()
    fun handleMarketplacePostSortAction()
    fun handleMarketplacePostSearchAction()
}