package com.hyouteki.oasis.bottomsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.activities.SettingsActivity
import com.hyouteki.oasis.comms.BottomSheetComms
import com.hyouteki.oasis.databinding.BottomSheetMoreBinding

class MoreBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: BottomSheetComms
    private lateinit var binding: BottomSheetMoreBinding
    private val user = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetMoreBinding.inflate(inflater, container, false)
        comm = activity as BottomSheetComms
        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.copy.setOnClickListener {
            comm.copyUserID()
            dismiss()
        }
        binding.signOut.setOnClickListener {
            comm.signOut()
            dismiss()
        }
        binding.settings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
            dismiss()
        }
    }

}