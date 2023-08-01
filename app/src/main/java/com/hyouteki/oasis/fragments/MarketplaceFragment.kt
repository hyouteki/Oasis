package com.hyouteki.oasis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.CustomFragment
import com.hyouteki.oasis.classes.MarketplacePostFireStoreAdapter
import com.hyouteki.oasis.classes.ScrollToTopObserverForMarketplacePost
import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.models.MarketplacePost

class MarketplaceFragment : CustomFragment("Marketplace", CustomFragment.ADD_SEARCH_ACTION) {
    private lateinit var postRecycler: RecyclerView
    private lateinit var myAdapter: MarketplacePostFireStoreAdapter
    private lateinit var comm: Communicator
    private val postCollections = PostDao().marketplacePostCollection
    private val query = postCollections.orderBy("pid", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<MarketplacePost>()
        .setQuery(query, MarketplacePost::class.java)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_marketplace, container, false)

        comm = activity as Communicator

        postRecycler = view.findViewById(R.id.rv_fm_recycler)
        setupRecyclerView()
        myAdapter.registerAdapterDataObserver(
            ScrollToTopObserverForMarketplacePost(
                postRecycler,
                myAdapter,
                LinearLayoutManager(requireContext())
            )
        )
        return view
    }

    private fun setupRecyclerView() {
        myAdapter =
            MarketplacePostFireStoreAdapter(comm, recyclerViewOptions, false, requireContext())
        postRecycler.adapter = myAdapter
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }
}