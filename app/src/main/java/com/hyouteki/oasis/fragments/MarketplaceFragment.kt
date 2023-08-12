package com.hyouteki.oasis.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.abstractions.MarketplaceTags
import com.hyouteki.oasis.abstractions.Parameters
import com.hyouteki.oasis.communicators.MainCommunicator
import com.hyouteki.oasis.databinding.FragmentMarketplaceBinding
import com.hyouteki.oasis.databinding.MarketplacePostListItemBinding
import com.hyouteki.oasis.databinding.MarketplaceTagsBinding
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel

class MarketplaceFragment : ModalFragment() {
    private lateinit var binding: FragmentMarketplaceBinding
    private lateinit var communicator: MainCommunicator

    private lateinit var adapter: MarketplacePostAdapter

    companion object {
        const val TAG = "MARKETPLACE_FRAGMENT"
        const val SORT_ACTION_ID = 0
        private var COLLECTION = OasisViewModel.postDAO.marketplacePostCollection
        private val DEFAULT_QUERY = COLLECTION.orderBy("postID", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarketplaceBinding.inflate(inflater, container, false)
        communicator = activity as MainCommunicator

        setupRecyclerView(DEFAULT_QUERY)
        handleSortUIComponents()

        return binding.root
    }

    private fun handleSortUIComponents() {
        binding.closeBottomNavigationBar.setOnClickListener {
            binding.bottomSecondaryBar.visibility = View.GONE
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView(query: Query = DEFAULT_QUERY) {
        query.limit(Parameters.PAGING_LIMIT.toLong()).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.documents.isNotEmpty()) {
                val dataset = arrayListOf<MarketplacePost>()
                for (document in task.result) {
                    dataset.add(document.toObject(MarketplacePost::class.java))
                }
                adapter.updateDataset(dataset)
                var lastVisible: DocumentSnapshot = task.result.documents.last()
                var isScrolling = false
                var isLastItemReached = false
                val onScrollListener: RecyclerView.OnScrollListener =
                    object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView, newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)
                            isScrolling =
                                isScrolling || newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            if (adapter.itemCount % Parameters.PAGING_LIMIT == 0) {
                                val linearLayoutManager =
                                    recyclerView.layoutManager as LinearLayoutManager
                                val firstVisibleItemPosition =
                                    linearLayoutManager.findFirstVisibleItemPosition()
                                val visibleItemCount = linearLayoutManager.childCount
                                val totalItemCount = linearLayoutManager.itemCount
                                if (isScrolling && firstVisibleItemPosition + visibleItemCount == totalItemCount && !isLastItemReached) {
                                    isScrolling = false
                                    query.startAfter(lastVisible)
                                        .limit(Parameters.PAGING_LIMIT.toLong()).get()
                                        .addOnCompleteListener { subTask ->
                                            if (subTask.isSuccessful && subTask.result.documents.isNotEmpty()) {
                                                for (subDocument in subTask.result) {
                                                    dataset.add(subDocument.toObject(MarketplacePost::class.java))
                                                }
                                                adapter.updateDataset(dataset)
                                                lastVisible = subTask.result.documents.last()
                                                isLastItemReached =
                                                    isLastItemReached || subTask.result.size() < Parameters.PAGING_LIMIT
                                            }
                                        }
                                }
                            }
                        }
                    }
                binding.recyclerView.addOnScrollListener(onScrollListener)
            }
        }

        adapter = MarketplacePostAdapter()
        binding.recyclerView.adapter = adapter
    }

    inner class MarketplacePostAdapter : RecyclerView.Adapter<MarketplacePostAdapter.ViewModel>() {

        private val dataset = arrayListOf<MarketplacePost>()

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
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewModel {
            return ViewModel(
                MarketplacePostListItemBinding.inflate(layoutInflater, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewModel, position: Int) {
            val model = dataset[position]
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
            holder.userName.setOnClickListener {
                communicator.handleMarketplacePostContactAction(model)
            }
        }

        override fun getItemCount() = dataset.size

        fun updateDataset(newDataset: List<MarketplacePost>) {
            dataset.clear()
            dataset.addAll(newDataset)
            adapter.notifyDataSetChanged()
        }
    }

    private fun handleSortAction() {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setTitle("Choose tags")
            val marketplaceTagsBinding = MarketplaceTagsBinding.inflate(layoutInflater)
            setView(marketplaceTagsBinding.root)
            var categoryChips = MarketplaceTags.getCategoryChips(marketplaceTagsBinding)
            val sellChips = MarketplaceTags.getSellChips(marketplaceTagsBinding)
            val conditionChips = MarketplaceTags.getConditionChips(marketplaceTagsBinding)
            MarketplaceTags.getSellChipSell(marketplaceTagsBinding).isChecked = false
            setPositiveButton("Save") { _, _ ->
                val categoryTags = arrayListOf<String>()
                var sellTag: String? = null
                var conditionTag: String? = null
                for (chip in categoryChips) {
                    if (chip.isChecked) {
                        categoryTags.add(chip.text.toString())
                        if (categoryTags.size == 3) {
                            break
                        }
                    }
                }
                for (chip in sellChips) {
                    if (chip.isChecked) {
                        sellTag = chip.text.toString()
                        break
                    }
                }
                for (chip in conditionChips) {
                    if (chip.isChecked) {
                        conditionTag = chip.text.toString()
                        break
                    }
                }
                if (MarketplaceTags.getCategoryTagGroup(marketplaceTagsBinding).checkedChipIds.size > 3) {
                    Toast.makeText(
                        requireContext(), "Select only three category tags", Toast.LENGTH_SHORT
                    ).show()
                }
                categoryChips = arrayListOf(
                    binding.categoryTag1, binding.categoryTag2, binding.categoryTag3
                )
                var query = DEFAULT_QUERY
                if (categoryTags.isNotEmpty()) {
                    query = query.whereArrayContainsAny("categoryTags", categoryTags)
                }
                for (chip in categoryChips) {
                    chip.visibility = View.GONE
                }
                for ((i, tag) in categoryTags.withIndex()) {
                    categoryChips[i].text = tag
                    categoryChips[i].visibility = View.VISIBLE
                }
                binding.sellTag.visibility = View.GONE
                if (sellTag != null) {
                    binding.sellTag.text = sellTag
                    binding.sellTag.visibility = View.VISIBLE
//                    query = query.whereEqualTo("sellTag", sellTag)
                }
                binding.conditionTag.visibility = View.GONE
                if (conditionTag != null) {
                    binding.conditionTag.text = conditionTag
                    binding.conditionTag.visibility = View.VISIBLE
//                    query = query.whereEqualTo("conditionTag", conditionTag)
                }
                binding.bottomSecondaryBar.visibility = View.VISIBLE
                setupRecyclerView(query)
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    override fun handleAction(actionId: Int) {
        when (actionId) {
            SORT_ACTION_ID -> handleSortAction()
        }
    }
}