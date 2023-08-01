package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.Communicator

class HistoryBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: Communicator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_history, container, false)
        val updateButton = view.findViewById<MaterialButton>(R.id.update)
        val deleteButton = view.findViewById<MaterialButton>(R.id.remove)
        comm = activity as Communicator
        updateButton.setOnClickListener {
            comm.updatePost()
            dismiss()
        }
        deleteButton.setOnClickListener {
            comm.deletePost()
            dismiss()
        }
        return view
    }
}