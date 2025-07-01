package com.example.thecodecup.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.thecodecup.fragment.HistoryFragment
import com.example.thecodecup.fragment.OnGoingFragment

class OrderPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) OnGoingFragment() else HistoryFragment()
    }
}
