package com.hyouteki.oasis.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.hyouteki.oasis.R
import com.hyouteki.oasis.bottomsheets.ChatBottomSheet
import com.hyouteki.oasis.classes.ChatFireStoreAdapter
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.ScrollToBottomObserver
import com.hyouteki.oasis.daos.UserDao
import com.hyouteki.oasis.models.Chat
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.utils.Helper
import com.hyouteki.oasis.utils.Logger
import com.hyouteki.oasis.utils.Saver
import com.hyouteki.oasis.viewmodels.OasisViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.time.Duration
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity(), Communicator {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var uid: String? = null
    private var startText: String? = null
    private lateinit var userImage: CircleImageView
    private lateinit var userName: TextView
    private lateinit var textBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: ChatFireStoreAdapter
    private var currentChat: Chat? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        window.statusBarColor = getColor(R.color.secBackground)
        uid = intent.getStringExtra("uid")
        startText = intent.getStringExtra("message")
        userImage = findViewById(R.id.user_image)
        userName = findViewById(R.id.user_name)
        textBox = findViewById(R.id.message_text)
        sendButton = findViewById(R.id.send)
        recyclerView = findViewById(R.id.rv_ac_recycler_view)
        val backButton: ImageView = findViewById(R.id.previous)

        sharedPreferences = Saver.getPreferences(this)

        setUp()

        textBox.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    sendButton.visibility = View.VISIBLE
                } else {
                    sendButton.visibility = View.GONE
                }
            }
        })

        sendButton.setOnClickListener { onSendButtonClick() }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setUp() {
        val documentReference = UserDao.collection.document(uid!!)
        documentReference.get().addOnSuccessListener { documentSnapshot ->
            val userReference = documentSnapshot.toObject(User::class.java)
            userReference?.uid?.let { Logger.debugger(it) }
            Glide.with(userImage.context).load(userReference?.photoUrl).circleCrop().into(userImage)
            userName.text = userReference?.displayName
        }
        textBox.setText(startText)
        setupRecyclerView()
        when (startText) {
            "" -> sendButton.visibility = View.GONE
            else -> sendButton.visibility = View.VISIBLE
        }
    }

    private fun onSendButtonClick() {
        if (textBox.text.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            var user1 = currentUser?.uid.toString()
            var user2 = uid!!
            var sender = false
            if (currentUser?.uid!! > uid!!) {
                user1 = uid!!
                user2 = currentUser.uid
                sender = true
            }
            val time = LocalDateTime.now().toString()
            val chat = Chat(
                id = currentTime.toString(),
                userId1 = user1,
                userId2 = user2,
                sender = sender,
                message = textBox.text.toString(),
                time = time
            )
            OasisViewModel.addChat(chat)
            myAdapter.registerAdapterDataObserver(
                ScrollToBottomObserver(recyclerView, myAdapter, LinearLayoutManager(this))
            )
            myAdapter.notifyDataSetChanged()
            textBox.setText("")
        }
    }

    private fun setupRecyclerView() {
        var user1 = currentUser!!.uid
        var user2 = uid!!
        if (user1 > user2) {
            user1 = uid!!
            user2 = currentUser.uid
        }
        val collection = OasisViewModel.chatCollection
        val query = collection
            .whereEqualTo("userId1", user1)
            .whereEqualTo("userId2", user2)
            .orderBy("id", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions
            .Builder<Chat>()
            .setQuery(query, Chat::class.java)
            .build()
        myAdapter = ChatFireStoreAdapter(this, recyclerViewOptions)
        recyclerView.adapter = myAdapter
    }

    override fun openChatOptions(chat: Chat) {
        currentChat = chat
        val duration: Duration = Duration.between(
            LocalDateTime.parse(chat.time),
            LocalDateTime.now()
        )
        val dialog = ChatBottomSheet()
        val bundle = Bundle()
        var delete = false
        if (duration.toMinutes().toInt() <= 42) {
            delete = when (chat.sender) {
                false -> {
                    (chat.userId1 == currentUser!!.uid)
                }
                true -> {
                    (chat.userId2 == currentUser!!.uid)
                }
            }
        }
        bundle.putBoolean("delete", delete)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "Chat#Dialog@Oasis")
    }

    override fun copyMessage() {
        currentChat?.let {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("MessageText@Oasis", it.message)
            clipboard.setPrimaryClip(clip)
            Helper.makeToast(this, "Copied to clipboard")
        }
    }

    override fun deleteMessage() {
        fun delete() {
            currentChat?.let {
                val documentReference = OasisViewModel.chatCollection.document(it.id)
                documentReference.delete().addOnSuccessListener {
                }.addOnFailureListener {
                    Helper.makeToast(this, "Cannot delete message")
                }
            }
        }
        when (sharedPreferences.getBoolean(Saver.CONFIRM_DELETE, false)) {
            true -> {
                with(MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogStyle)) {
                    setTitle("Confirm delete")
                    setMessage("Are you sure you want to delete?")
                    setPositiveButton("Yes") { _, _ ->
                        delete()
                    }
                    setNegativeButton("No") { _, _ ->

                    }
                    show()
                }
            }
            false -> {
                delete()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }
}