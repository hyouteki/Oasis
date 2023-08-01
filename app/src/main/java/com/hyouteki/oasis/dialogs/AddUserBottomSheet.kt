package com.hyouteki.oasis.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.utils.Helper

class AddUserBottomSheet : BottomSheetDialogFragment() {
    private lateinit var comm: Communicator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_user, container, false)
        val uid = view.findViewById<TextInputEditText>(R.id.tiet_bsau_uid)
        val addButton = view.findViewById<Button>(R.id.btn_bsau_add)
        comm = activity as Communicator
        addButton.setOnClickListener {
            if (uid.text.toString().isNotEmpty()) {
                comm.launchChatActivity(uid.text.toString())
                dismiss()
            } else {
                Helper.makeToast(requireContext(), "Enter user ID")
            }
        }
        return view
    }
}