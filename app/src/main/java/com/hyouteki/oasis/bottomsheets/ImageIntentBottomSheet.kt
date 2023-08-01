package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.oasis.comms.BottomSheetComms
import com.hyouteki.oasis.databinding.BottomSheetImageIntentBinding

class ImageIntentBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetImageIntentBinding
    private lateinit var comm: BottomSheetComms

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetImageIntentBinding.inflate(inflater, container, false)
        initialize()
        handleTouches()
        return binding.root
    }

    private fun initialize() {
        comm = activity as BottomSheetComms
    }

    private fun handleTouches() {
        binding.camera.setOnClickListener {
            comm.imageIntentTypeCamera()
            dismiss()
        }
        binding.photos.setOnClickListener {
            comm.imageIntentTypePhotos()
            dismiss()
        }
    }
}