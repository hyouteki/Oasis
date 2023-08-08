package com.hyouteki.oasis.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.communicators.MainCommunicator
import com.hyouteki.oasis.databinding.FragmentMarketplaceBinding
import com.hyouteki.oasis.databinding.MarketplacePostListItemBinding
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel


class MarketplaceFragment : Fragment() {
    private lateinit var binding: FragmentMarketplaceBinding
    private lateinit var communicator: MainCommunicator
    private val firebaseQuery = OasisViewModel.postDAO.marketplacePostCollection.orderBy(
        "postID", Query.Direction.DESCENDING
    )
    private lateinit var adapter: MarketplacePostFireStoreAdapter
    private val recyclerViewOptions = FirestoreRecyclerOptions.Builder<MarketplacePost>()
        .setQuery(firebaseQuery, MarketplacePost::class.java).build()

    companion object {
        const val TAG = "MARKETPLACE_FRAGMENT"
        const val PAGING_LIMIT = 20
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarketplaceBinding.inflate(inflater, container, false)
        communicator = activity as MainCommunicator

        initialization()
        setupRecyclerView()

        return binding.root
    }

    private fun initialization() {
        class ScrollToTopObserverForMarketplacePost(
            private val recycler: RecyclerView,
            private val adapter: MarketplaceFragment.MarketplacePostFireStoreAdapter,
            private val manager: LinearLayoutManager
        ) : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val count = adapter.itemCount
                val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                val loading = lastVisiblePosition == -1
                val atBottom =
                    positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
                if (loading || atBottom) {
                    recycler.smoothScrollToPosition(positionStart)
                }
            }
        }
        setupRecyclerView()
        adapter.registerAdapterDataObserver(
            ScrollToTopObserverForMarketplacePost(
                binding.recyclerView,
                adapter,
                binding.recyclerView.layoutManager as LinearLayoutManager
            )
        )

    }

    private fun setupRecyclerView() {
        adapter = MarketplacePostFireStoreAdapter(recyclerViewOptions)
        binding.recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    inner class MarketplacePostFireStoreAdapter(
        options: FirestoreRecyclerOptions<MarketplacePost>
    ) : FirestoreRecyclerAdapter<MarketplacePost, MarketplacePostFireStoreAdapter.ViewModel>(
        options
    ) {
        inner class ViewModel(marketplacePostListItemBinding: MarketplacePostListItemBinding) :
            RecyclerView.ViewHolder(marketplacePostListItemBinding.root) {
            val userName = marketplacePostListItemBinding.userName
            val itemName = marketplacePostListItemBinding.itemName
            val itemDesc = marketplacePostListItemBinding.itemDesc
            val itemPrice = marketplacePostListItemBinding.itemPrice
            val categoryChips = arrayListOf(
                marketplacePostListItemBinding.categoryTag1,
                marketplacePostListItemBinding.categoryTag2,
                marketplacePostListItemBinding.categoryTag3
            )
            val sellChip = marketplacePostListItemBinding.sellTag
            val conditionChip = marketplacePostListItemBinding.conditionTag
            val contactButton = marketplacePostListItemBinding.contact
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewModel {
            return ViewModel(
                MarketplacePostListItemBinding.inflate(layoutInflater, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: ViewModel, position: Int, model: MarketplacePost
        ) {
            OasisViewModel.getUserDocument(model.userID).addOnSuccessListener {
                it.toObject(User::class.java)?.let { user ->
                    holder.userName.text = "@${user.name}"
                }
            }
            holder.itemName.text = model.itemName
            holder.itemDesc.text = model.itemDesc
            holder.itemPrice.text = model.itemPrice.ifEmpty {
                "FREE"
            }
            for (chip in holder.categoryChips) {
                chip.visibility = View.GONE
            }
            for ((i, tag) in model.categoryTags.withIndex()) {
                if (tag.isNotEmpty()) {
                    holder.categoryChips[i].text = tag
                    holder.categoryChips[i].visibility = View.VISIBLE
                }
            }
            holder.sellChip.text = model.sellTag
            if (model.conditionTag.isNotEmpty()) {
                holder.conditionChip.text = model.conditionTag
                holder.conditionChip.visibility = View.VISIBLE
            } else {
                holder.conditionChip.visibility = View.GONE
            }
            holder.contactButton.setOnClickListener {
                communicator.handleMarketplacePostContactAction(model)
            }
        }

        override fun getItemViewType(position: Int) = position
    }
}