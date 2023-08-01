package com.hyouteki.oasis.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyouteki.oasis.R
import com.hyouteki.oasis.databinding.FragmentEventBinding
import com.hyouteki.oasis.databinding.FragmentHappeningBinding

class EventFragment : Fragment() {
    private lateinit var binding: FragmentEventBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventBinding.inflate(inflater, container, false)

//        initialize()

        return binding.root
    }
}