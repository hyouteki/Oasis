package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.databinding.BottomSheetChatUserBinding

class ChatUserBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: Communicator
    private lateinit var binding: BottomSheetChatUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetChatUserBinding.inflate(inflater, container, false)
        comm = activity as Communicator
        binding.copy.setOnClickListener {
            comm.copyUserId()
            dismiss()
        }
        binding.remove.setOnClickListener {
            comm.removeUser()
            dismiss()
        }
        return binding.root
    }
}