package com.hyouteki.oasis.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.MarketplacePostFireStoreAdapter
import com.hyouteki.oasis.daos.PostDao
import com.hyouteki.oasis.databinding.ActivityMarketplaceHistoryBinding
import com.hyouteki.oasis.models.MarketplacePost

class MarketplaceHistoryActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ActivityMarketplaceHistoryBinding
    private lateinit var marketplaceAdapter: MarketplacePostFireStoreAdapter
    private val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceHistoryBinding.inflate(layoutInflater)
        window.statusBarColor = getColor(R.color.secBackground)
        setContentView(binding.root)
        setupRecyclerView()
        handleTouches()
    }

    private fun handleTouches() {
        binding.previous.setOnClickListener { finish() }
        fun getSelectedChips(): ArrayList<String> {
            val selectedChips = arrayListOf<String>()
            if (binding.tagSell.isChecked) {
                selectedChips.add(MarketplacePost.SELL)
            }
            if (binding.tagLend.isChecked) {
                selectedChips.add(MarketplacePost.LEND)
            }
            if (binding.tagLost.isChecked) {
                selectedChips.add(MarketplacePost.LOST)
            }
            if (binding.tagFound.isChecked) {
                selectedChips.add(MarketplacePost.FOUND)
            }
            return selectedChips
        }
        binding.tagGroup.setOnCheckedStateChangeListener { _, _ ->
            setupRecyclerView(getSelectedChips())
        }
    }

    private fun setupRecyclerView(
        require: ArrayList<String> = arrayListOf(
            MarketplacePost.SELL,
            MarketplacePost.LEND,
            MarketplacePost.LOST,
            MarketplacePost.FOUND
        )
    ) {
        if (require.isNotEmpty()) {
            binding.progress.visibility = View.VISIBLE
            binding.recycler.visibility = View.GONE
            val marketplaceQuery =
                PostDao().marketplacePostCollection.whereEqualTo("uid", user.uid)
                    .whereIn("postType", require)
                    .orderBy("pid", Query.Direction.DESCENDING)
            val marketplaceOptions = FirestoreRecyclerOptions.Builder<MarketplacePost>()
                .setQuery(marketplaceQuery, MarketplacePost::class.java).build()
            marketplaceAdapter =
                MarketplacePostFireStoreAdapter(this, marketplaceOptions, true, this)
            binding.recycler.adapter = marketplaceAdapter
            binding.progress.visibility = View.GONE
            binding.recycler.visibility = View.VISIBLE
        }
    }

    override fun handlePostAction(post: MarketplacePost) {

    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        marketplaceAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        setupRecyclerView()
        marketplaceAdapter.stopListening()
    }

}