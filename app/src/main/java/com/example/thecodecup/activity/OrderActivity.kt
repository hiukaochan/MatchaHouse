package com.example.thecodecup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.thecodecup.R
import com.example.thecodecup.adapter.OrderPagerAdapter
import android.widget.ImageView

class OrderActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: OrderPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val pagerAdapter = OrderPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val text = if (position == 0) "On Going" else "History"
            tab.text = text
            tab.contentDescription = "$text tab"
        }.attach()
    }

}
