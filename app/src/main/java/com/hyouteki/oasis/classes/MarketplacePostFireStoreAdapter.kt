package com.hyouteki.oasis.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hyouteki.oasis.R
import com.hyouteki.oasis.activities.AddMarketplacePostActivity
import com.hyouteki.oasis.models.MarketplacePost
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel
import de.hdodenhof.circleimageview.CircleImageView

class MarketplacePostFireStoreAdapter(
    private val listener: Communicator,
    options: FirestoreRecyclerOptions<MarketplacePost>,
    private val flag: Boolean = false,
    private val context: Context
) :
    FirestoreRecyclerAdapter<MarketplacePost, MarketplacePostFireStoreAdapter.MarketplacePostFireStoreViewModel>(
        options
    ) {
    inner class MarketplacePostFireStoreViewModel(view: View) :
        RecyclerView.ViewHolder(view) {
        val everything: ConstraintLayout = view.findViewById(R.id.cl_mli_everything)
        val userImage: CircleImageView = view.findViewById(R.id.profile)
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val tags: ChipGroup = view.findViewById(R.id.tag_group)
        val tagsImage: LinearLayout = view.findViewById(R.id.tags_image)
        val productImage: CardView = view.findViewById(R.id.image_card)
        val colorStrip1: View = view.findViewById(R.id.color_strip_1)
        val colorStrip2: View = view.findViewById(R.id.color_strip_2)
        val ruppee: TextView = view.findViewById(R.id.ruppee)
        val tag1: Chip = view.findViewById(R.id.tag_1)
        val tag2: Chip = view.findViewById(R.id.tag_2)
        val tag3: Chip = view.findViewById(R.id.tag_3)
        val tag1Image: Chip = view.findViewById(R.id.tag_1_image)
        val tag2Image: Chip = view.findViewById(R.id.tag_2_image)
        val tag3Image: Chip = view.findViewById(R.id.tag_3_image)
        val detailsCard: CardView = view.findViewById(R.id.details_card)
        val details: ConstraintLayout = view.findViewById(R.id.details)
        val gap: View = view.findViewById(R.id.gap)
        val space: View = view.findViewById(R.id.space)
        val separator: View = view.findViewById(R.id.separator)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemDesc: TextView = view.findViewById(R.id.item_desc)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val itemType: CardView = view.findViewById(R.id.item_type)
        val itemType2: CardView = view.findViewById(R.id.item_type_2)
        val itemTypeText: TextView = view.findViewById(R.id.item_type_text)
        val itemType2Text: TextView = view.findViewById(R.id.item_type_2_text)
        val contactButton: Button = view.findViewById(R.id.contact)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarketplacePostFireStoreViewModel {
        return MarketplacePostFireStoreViewModel(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.marketplace_post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: MarketplacePostFireStoreViewModel,
        position: Int,
        model: MarketplacePost
    ) {
        OasisViewModel.getUserDocument(model.uid).addOnSuccessListener {
            it.toObject(User::class.java)?.let { user ->
                Glide.with(holder.userImage.context)
                    .load(user.photoUrl)
                    .into(holder.userImage)
            }
        }

        if (model.imageUrl1 == "null") {
            holder.productImage.visibility = View.GONE
            holder.space.visibility = View.VISIBLE
            holder.separator.visibility = View.VISIBLE
            holder.itemType.visibility = View.GONE
            holder.itemType2.visibility = View.VISIBLE
        } else {
            holder.details.visibility = View.GONE
            holder.tagsImage.visibility = View.VISIBLE
            Glide.with(holder.itemImage.context)
                .load(model.imageUrl1)
                .dontAnimate()
                .into(holder.itemImage)
            holder.itemType.visibility = View.VISIBLE
            holder.itemType2.visibility = View.GONE
        }

        holder.itemName.text = model.itemName
        holder.itemDesc.text = model.itemDesc
        holder.itemPrice.text = model.itemPrice

        if (model.itemType == AddMarketplacePostActivity.PRODUCT) {
            holder.colorStrip1.setBackgroundResource(R.color.midnightPurple)
            holder.colorStrip2.setBackgroundResource(R.color.midnightPurple)
        } else {
            holder.colorStrip1.setBackgroundResource(R.color.spaceRed)
            holder.colorStrip2.setBackgroundResource(R.color.spaceRed)
        }

        when (model.postType) {
            "Sell" -> {
                holder.itemTypeText.text = model.postType
                holder.itemType2Text.text = model.postType
                holder.itemType.setCardBackgroundColor(getColor(context, R.color.spaceRed))
                holder.itemType2.setCardBackgroundColor(getColor(context, R.color.spaceRed))
            }
            "Lend" -> {
                holder.itemTypeText.text = "Lending for ${model.lendingTime} days"
                holder.itemType2Text.text = "Lending for ${model.lendingTime} days"
                holder.itemType.setCardBackgroundColor(getColor(context, R.color.OasisMedium))
                holder.itemType2.setCardBackgroundColor(getColor(context, R.color.OasisMedium))
            }
            "Lost" -> {
                holder.ruppee.visibility = View.GONE
                holder.itemPrice.visibility = View.GONE
                holder.itemTypeText.text = model.postType
                holder.itemType2Text.text = model.postType
                holder.itemType.setCardBackgroundColor(getColor(context, R.color.purple))
                holder.itemType2.setCardBackgroundColor(getColor(context, R.color.purple))
            }
            "Found" -> {
                holder.ruppee.visibility = View.GONE
                holder.itemPrice.visibility = View.GONE
                holder.itemTypeText.text = model.postType
                holder.itemType2Text.text = model.postType
                holder.itemType.setCardBackgroundColor(getColor(context, R.color.lightGreen))
                holder.itemType2.setCardBackgroundColor(getColor(context, R.color.lightGreen))
            }
        }

        if (model.tag1 == "null" && model.tag2 == "null" && model.tag3 == "null") {
            holder.tags.visibility = View.GONE
            holder.tagsImage.visibility = View.INVISIBLE
        }
        if (model.tag1 != "null") {
            holder.tag1.text = model.tag1
            holder.tag1Image.text = model.tag1
        } else {
            holder.tag1.visibility = View.GONE
            holder.tag1Image.visibility = View.GONE
        }
        if (model.tag2 != "null") {
            holder.tag2.text = model.tag2
            holder.tag2Image.text = model.tag2
        } else {
            holder.tag2.visibility = View.GONE
            holder.tag2Image.visibility = View.GONE
        }
        if (model.tag3 != "null") {
            holder.tag3.text = model.tag3
            holder.tag3Image.text = model.tag3
        } else {
            holder.tag3.visibility = View.GONE
            holder.tag3Image.visibility = View.GONE
        }

        holder.detailsCard.setOnClickListener {
            if (holder.productImage.visibility == View.VISIBLE) {
                if (holder.details.visibility == View.VISIBLE) {
                    holder.details.visibility = View.GONE
                    holder.tagsImage.visibility = View.VISIBLE
                } else {
                    holder.details.visibility = View.VISIBLE
                    holder.tagsImage.visibility = View.GONE
                }
            }
        }
        holder.detailsCard.setOnLongClickListener {
            if (flag) {
                listener.handlePostAction(model)
            }
            true
        }
        holder.contactButton.setOnClickListener {
            listener.handleContact(model)
        }
        holder.everything.setOnLongClickListener {
            if (flag) {
                listener.handlePostAction(model)
            }
            true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}