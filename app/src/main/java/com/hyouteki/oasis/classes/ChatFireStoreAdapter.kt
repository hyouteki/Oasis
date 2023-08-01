package com.hyouteki.oasis.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.R
import com.hyouteki.oasis.models.Chat
import com.hyouteki.oasis.utils.Helper

class ChatFireStoreAdapter(
    private val listener: Communicator,
    options: FirestoreRecyclerOptions<Chat>
) :
    FirestoreRecyclerAdapter<Chat, ChatFireStoreAdapter.ChatViewModel>(options) {
    val user = FirebaseAuth.getInstance().currentUser

    inner class ChatViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val message: ConstraintLayout = view.findViewById(R.id.cl_cli_message)
        val cardLeft: CardView = view.findViewById(R.id.card_left)
        val backgroundLeft: ConstraintLayout = view.findViewById(R.id.background_left)
        val textLeft: TextView = view.findViewById(R.id.text_box_left)
        val timeLeft: TextView = view.findViewById(R.id.time_left)
        val cardRight: CardView = view.findViewById(R.id.card_right)
        val backgroundRight: ConstraintLayout = view.findViewById(R.id.background_right)
        val textRight: TextView = view.findViewById(R.id.text_box_right)
        val timeRight: TextView = view.findViewById(R.id.time_right)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewModel {
        return ChatViewModel(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ChatViewModel,
        position: Int,
        model: Chat
    ) {
        holder.message.setOnLongClickListener {
            listener.openChatOptions(model)
            true
        }
        fun sender() {
            holder.textRight.text = model.message
            holder.timeRight.text = Helper.formatTime(model.time)
            holder.backgroundRight.setBackgroundResource(R.color.foreground)
            holder.cardLeft.visibility = View.GONE
            holder.cardRight.visibility = View.VISIBLE
        }

        fun receiver() {
            holder.textLeft.text = model.message
            holder.timeLeft.text = Helper.formatTime(model.time)
            holder.backgroundLeft.setBackgroundResource(R.color.colorTertiary)
            holder.cardLeft.visibility = View.VISIBLE
            holder.cardRight.visibility = View.GONE
        }
        when (user!!.uid == model.userId2) {
            true -> { // me is second user
                when (model.sender) {
                    true -> { // sender is second user
                        sender()
                    }
                    false -> { // sender is first user
                        receiver()
                    }
                }
            }
            false -> { // me is first user
                when (model.sender) {
                    false -> { // sender is first user
                        sender()
                    }
                    true -> { // sender is second user
                        receiver()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}