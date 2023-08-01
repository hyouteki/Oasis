package com.hyouteki.oasis.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyouteki.oasis.R
import com.hyouteki.oasis.adapters.ViewPagerAdapter
import com.hyouteki.oasis.databinding.FragmentHappeningBinding

class HappeningFragment : Fragment() {
    private lateinit var binding: FragmentHappeningBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHappeningBinding.inflate(inflater, container, false)

        initialize()

        return binding.root
    }

    private fun initialize() {
        initTabs()
    }

    private fun initTabs() {
        val adapter = ViewPagerAdapter(childFragmentManager)
        val tabCount = 2
        val fragments = arrayListOf(ConfessionFragment(), EventFragment())
        val titles = arrayListOf("Confessions", "Events")
        val icons = arrayListOf(R.drawable.ic_note, R.drawable.ic_event)
        for (i in 0 until tabCount) {
            adapter.addFragment(fragments[i], titles[i])
        }
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        for (i in 0 until tabCount) {
            binding.tabs.getTabAt(i)?.setIcon(icons[i])
        }
    }
}