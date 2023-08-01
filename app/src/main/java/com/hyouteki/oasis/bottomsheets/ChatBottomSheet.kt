package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.databinding.BottomSheetChatBinding

class ChatBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: Communicator
    private lateinit var binding: BottomSheetChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetChatBinding.inflate(inflater, container, false)
        binding.delete.isEnabled = arguments?.getBoolean("delete", false) == true
        if (!binding.delete.isEnabled) {
            binding.deleteIcon.imageTintList = activity?.getColorStateList(R.color.colorSecBackground)
            binding.deleteText.setTextColor(activity!!.getColor(R.color.colorSecBackground))
        }
        comm = activity as Communicator
        binding.copy.setOnClickListener {
            comm.copyMessage()
            dismiss()
        }
        binding.delete.setOnClickListener {
            comm.deleteMessage()
            dismiss()
        }
        return binding.root
    }
}