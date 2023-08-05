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
import com.hyouteki.oasis.databinding.CustomBottomSheetBinding
import com.hyouteki.oasis.databinding.CustomBottomSheetListItemBinding

open class ModalBottomSheet(
    private val itemTexts: ArrayList<String>,
    private val itemImages: ArrayList<Int>
) : BottomSheetDialogFragment() {

    private lateinit var binding: CustomBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.adapter = ItemAdapter()
    }

    open inner class ItemAdapter internal constructor() :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.image)
            val text: TextView = view.findViewById(R.id.text)
            val button: ConstraintLayout = view.findViewById(R.id.button)
        }

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
                handleAction(position)
                dismiss()
            }
        }

        override fun getItemCount() = itemTexts.size
    }

    open fun handleAction(position: Int) {}
}