package com.hyouteki.oasis.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.activities.MarketplaceHistoryActivity
import com.hyouteki.oasis.bottomsheets.MoreBottomSheet
import com.hyouteki.oasis.comms.FragMainComms
import com.hyouteki.oasis.databinding.FragmentMoreBinding
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel


class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private val user = FirebaseAuth.getInstance().currentUser!!
    private lateinit var comm: FragMainComms
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        comm = activity as FragMainComms
        initialize()
        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.menu.setOnClickListener {
            MoreBottomSheet().show(childFragmentManager, "More#BottomSheet@Oasis")
        }
        binding.add.setOnClickListener {
            comm.switchToAddFragment()
        }
        binding.marketplace.setOnClickListener {
            startActivity(Intent(requireContext(), MarketplaceHistoryActivity::class.java))
        }
    }

    private fun initialize() {
        binding.historyCard.visibility = View.INVISIBLE
        OasisViewModel.getUserDocument(user.uid)
            .addOnSuccessListener {
                it.toObject(User::class.java)?.let { trash ->
                    binding.userName.text = trash.displayName
                    binding.userBio.text = trash.bio
                    Glide.with(binding.userImage.context)
                        .load(trash.photoUrl)
                        .into(binding.userImage)
                    binding.historyCard.visibility = View.VISIBLE
                }
            }
    }

}