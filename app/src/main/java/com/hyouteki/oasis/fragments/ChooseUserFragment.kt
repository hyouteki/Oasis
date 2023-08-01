package com.hyouteki.oasis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.hyouteki.oasis.R
import com.hyouteki.oasis.classes.ChatUserAdapter
import com.hyouteki.oasis.classes.Communicator
import com.hyouteki.oasis.classes.CustomFragment
import com.hyouteki.oasis.databinding.FragmentChooseUserBinding
import com.hyouteki.oasis.models.User
import com.hyouteki.oasis.utils.Logger
import com.hyouteki.oasis.viewmodels.OasisViewModel

class ChooseUserFragment : CustomFragment("Chat", CustomFragment.ADD_ACTION) {
    private val user = FirebaseAuth.getInstance().currentUser!!
    private lateinit var myAdapter: ChatUserAdapter
    private lateinit var comm: Communicator
    private lateinit var binding: FragmentChooseUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseUserBinding.inflate(inflater, container, false)
        comm = activity as Communicator
        initChatUsers()
        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.add.setOnClickListener {
            with(MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogStyle)) {
                setTitle("Type in user ID")
                val view: View = layoutInflater.inflate(R.layout.user_id_picker, null)
                setView(view)
                val userId: TextInputEditText = view.findViewById(R.id.user_id)
                setPositiveButton("Save") { _, _ ->
                    comm.onUserClick(userId.text.toString())
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
    }

    private fun initChatUsers() {
        myAdapter = ChatUserAdapter(comm, requireContext())
        binding.progress.visibility = View.VISIBLE
        binding.recycler.visibility = View.GONE
        OasisViewModel.getUserDocument(user.uid).addOnSuccessListener {
            it.toObject(User::class.java)?.let { temp ->
                myAdapter.updateData(temp.users)
                binding.recycler.adapter = myAdapter
                Logger.info("length is ${myAdapter.itemCount}")
                binding.progress.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
            }
        }
    }
}