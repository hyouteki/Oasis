package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.oasis.comms.BottomSheetComms
import com.hyouteki.oasis.databinding.BottomSheetConfessionOptionsBinding

class ConfessionOptionsBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetConfessionOptionsBinding
    private lateinit var comm: BottomSheetComms

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetConfessionOptionsBinding.inflate(inflater)
        comm = activity as BottomSheetComms
        binding.reply.setOnClickListener {
            comm.replyToConfession()
            dismiss()
        }
        return binding.root
    }
}