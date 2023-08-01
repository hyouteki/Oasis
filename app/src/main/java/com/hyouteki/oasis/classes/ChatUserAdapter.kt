package com.hyouteki.oasis.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyouteki.oasis.R
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.viewmodels.OasisViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatUserAdapter(
    private val listener: Communicator,
    private val context: Context
) : RecyclerView.Adapter<ChatUserAdapter.ChatUserViewModel>() {

    private val dataSet = arrayListOf<String>()

    inner class ChatUserViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: CircleImageView = view.findViewById(R.id.user_image)
        val userName: TextView = view.findViewById(R.id.user_name)
        val userBio: TextView = view.findViewById(R.id.user_bio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewModel {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_user_list_item, parent, false)
        val viewHolder = ChatUserViewModel(view)
        view.setOnClickListener {
            listener.onUserClick(dataSet[viewHolder.adapterPosition])
        }
        view.setOnLongClickListener {
            listener.openChatUserOptions(dataSet[viewHolder.adapterPosition])
            true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatUserViewModel, position: Int) {
        val model = dataSet[position]
        OasisViewModel.getUserDocument(model).addOnSuccessListener {
            it?.let {
                it.toObject(User::class.java)?.let { user ->
                    Glide.with(holder.userImage.context)
                        .load(user.photoUrl)
                        .into(holder.userImage)
                    holder.userName.text = user.displayName
                    holder.userBio.text = user.bio
                }
            }
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newDataSet: List<String>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = dataSet[position]
}