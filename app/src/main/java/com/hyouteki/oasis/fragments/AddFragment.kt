package com.hyouteki.oasis.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyouteki.oasis.activities.AddConfessionActivity
import com.hyouteki.oasis.activities.AddMarketplacePostActivity
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.CustomFragment
import com.hyouteki.oasis.databinding.FragmentAddBinding

class AddFragment : CustomFragment("Add", CustomFragment.NO_ACTION) {
    private lateinit var binding: FragmentAddBinding
    private lateinit var comm: Communicator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.marketplace.setOnClickListener {
            startActivity(Intent(requireContext(), AddMarketplacePostActivity::class.java))
        }
        binding.lostAndFound.setOnClickListener {
            val intent = Intent(requireContext(), AddMarketplacePostActivity::class.java)
            intent.putExtra("postType", "Lost")
            startActivity(intent)
        }
        binding.confession.setOnClickListener {
            startActivity(Intent(requireContext(), AddConfessionActivity::class.java))
        }
    }
}