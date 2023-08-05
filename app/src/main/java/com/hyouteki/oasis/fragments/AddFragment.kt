package com.hyouteki.oasis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyouteki.oasis.activities.MainActivity
import com.hyouteki.oasis.communicators.MainCommunicator
import com.hyouteki.oasis.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var communicator: MainCommunicator

    companion object {
        const val TAG = "ADD_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        communicator = activity as MainCommunicator

        handleTouches()
        return binding.root
    }

    private fun handleTouches() {
        binding.marketplace.setOnClickListener {
            communicator.handleMarketplacePostAddAction()
        }
    }
}