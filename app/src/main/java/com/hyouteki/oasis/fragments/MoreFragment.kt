package com.hyouteki.oasis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.databinding.FragmentMoreBinding
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel

class MoreFragment : ModalFragment() {
    private lateinit var binding: FragmentMoreBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    companion object {
        const val TAG = "FRAGMENT_MORE_BINDING"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        handleInitializeUIComponents()
        return binding.root
    }

    private fun handleInitializeUIComponents() {
        binding.historyCard.visibility = View.INVISIBLE
        binding.linearProgressBar.visibility = View.VISIBLE
        OasisViewModel.getUserDocument(currentUser.uid).addOnSuccessListener {
            it.toObject(User::class.java)?.let { user ->
                Glide.with(binding.userImage.context).load(user.photoUrl).into(binding.userImage)
                binding.historyCard.visibility = View.VISIBLE
                binding.linearProgressBar.visibility = View.GONE
                binding.userName.text = "@${user.name}"
                binding.userBio.text = user.bio
            }
        }
    }
}