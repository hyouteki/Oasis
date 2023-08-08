package com.hyouteki.oasis.communicators

import com.hyouteki.oasis.models.MarketplacePost

interface MainCommunicator {
    fun handleMarketplacePostAddAction()
    fun handleMarketplacePostContactAction(marketplacePost: MarketplacePost)
}