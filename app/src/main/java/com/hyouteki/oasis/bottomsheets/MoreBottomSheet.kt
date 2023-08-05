package com.hyouteki.oasis.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyouteki.oasis.R
import com.hyouteki.oasis.communicators.MoreCommunicator
import com.hyouteki.oasis.databinding.CustomBottomSheetBinding
import com.hyouteki.oasis.databinding.CustomBottomSheetListItemBinding

class MoreBottomSheet(val listener: MoreCommunicator) : BottomSheetDialogFragment() {

    private lateinit var binding: CustomBottomSheetBinding

    companion object {
        const val TAG = "MORE_BOTTOM_SHEET"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = ItemAdapter()
    }

    private inner class ViewHolder internal constructor(binding: CustomBottomSheetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val text: TextView = binding.text
    }

    private inner class ItemAdapter internal constructor() :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.image)
            val text: TextView = view.findViewById(R.id.text)
            val button: ConstraintLayout = view.findViewById(R.id.button)
        }

        private val itemTexts = arrayListOf(
            "Add post",
            "Copy user ID",
            "Edit",
            "Saved",
            "Invite",
            "Settings",
            "Sign out",
            "2023 Hyouteki"
        )

        private val itemImages = arrayListOf(
            R.drawable.add_box_outlined,
            R.drawable.copy,
            R.drawable.edit,
            R.drawable.saved,
            R.drawable.invite,
            R.drawable.settings,
            R.drawable.log_out,
            R.drawable.copyright
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.custom_bottom_sheet_list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = itemTexts[position]
            holder.image.setImageResource(itemImages[position])
            holder.button.setOnClickListener {
                when (position) {
                    0 -> listener.handlePostAddAction()
                    1 -> listener.handleUserIDCopyAction()
                    2 -> listener.handleProfileEditAction()
                    3 -> listener.handleSavedAction()
                    4 -> listener.handleInviteAction()
                    5 -> listener.handleSettingsAction()
                    6 -> listener.handleSignOutAction()
                    7 -> listener.handleCopyrightAction()
                }
                dismiss()
            }
        }

        override fun getItemCount() = itemTexts.size
    }
}