package com.hyouteki.oasis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.adapters.ConfessionAdapter
import com.hyouteki.oasis.bottomsheets.ConfessionOptionsBottomSheet
import com.hyouteki.oasis.comms.AdapterComms
import com.hyouteki.oasis.daos.ConfessionDao
import com.hyouteki.oasis.databinding.FragmentConfessionBinding
import com.hyouteki.oasis.models.Confession

class ConfessionFragment : Fragment(), AdapterComms {
    private lateinit var binding: FragmentConfessionBinding
    private lateinit var adapter: ConfessionAdapter
    private val collection = ConfessionDao.getCollection()
    private val query = collection.orderBy("id", Query.Direction.DESCENDING)
    private val recyclerViewOptions = FirestoreRecyclerOptions
        .Builder<Confession>()
        .setQuery(query, Confession::class.java)
        .build()
    var confessionRn: Confession? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfessionBinding.inflate(inflater, container, false)

        initialize()

        return binding.root
    }

    private fun initialize() {
        setupRecyclerView()
    }

    override fun onConfessionLongClicked(model: Confession) {
        confessionRn = model
        ConfessionOptionsBottomSheet().show(
            childFragmentManager,
            "ConfessionOptions#BottomSheet@Oasis"
        )
    }

    private fun setupRecyclerView() {
        adapter =
            ConfessionAdapter(this, recyclerViewOptions, requireContext())
        binding.recycler.adapter = adapter
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
}