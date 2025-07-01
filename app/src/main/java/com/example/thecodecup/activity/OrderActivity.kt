package com.example.thecodecup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.thecodecup.R
import com.example.thecodecup.adapter.OrderPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_order // Or nav_menu / nav_order based on activity

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_menu -> {
                    if (this !is MenuActivity) {
                        startActivity(Intent(this, MenuActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                R.id.nav_gift -> {
                    if (this !is GiftActivity) {
                        startActivity(Intent(this, GiftActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                R.id.nav_order -> {
                    if (this !is OrderActivity) {
                        startActivity(Intent(this, OrderActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                else -> false
            }
        }

    }

}
