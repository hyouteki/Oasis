package com.hyouteki.oasis.abstractions

import com.hyouteki.oasis.databinding.MarketplaceTagsBinding

interface MarketplaceTags {
    companion object {
        fun getCategoryChips(binding: MarketplaceTagsBinding) = arrayListOf(
            binding.categoryTagFood,
            binding.categoryTagCloth,
            binding.categoryTagElectronic,
            binding.categoryTagStationary,
            binding.categoryTagArt,
            binding.categoryTagDigital,
            binding.categoryTagNotes,
            binding.categoryTagFurniture,
            binding.categoryTagOther
        )

        fun getSellChips(binding: MarketplaceTagsBinding) = arrayListOf(
            binding.sellTagSell,
            binding.sellTagLend,
            binding.sellTagLost,
            binding.sellTagFound,
        )

        fun getConditionChips(binding: MarketplaceTagsBinding) = arrayListOf(
            binding.conditionTagNew,
            binding.conditionTagDecent,
            binding.conditionTagRefurbished,
            binding.conditionTagOld,
        )

        fun getSellChipSell(binding: MarketplaceTagsBinding) = binding.sellTagSell
        fun getSellChipLend(binding: MarketplaceTagsBinding) = binding.sellTagLend

        fun getCategoryTagGroup(binding: MarketplaceTagsBinding) = binding.categoryTagGroup
    }
}