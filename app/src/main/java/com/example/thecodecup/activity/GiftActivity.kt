package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class GiftActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gift)

        db = AppDatabase.getInstance(this)
        val recyclerView = findViewById<RecyclerView>(R.id.pointsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val userProfile = db.userProfileDao().getProfile()
            if (userProfile != null) updateUserInfo(userProfile.fullName, userProfile.drinkCount, userProfile.points)
        }

        val backButton = findViewById<Button>(R.id.redeemButton)
        backButton.setOnClickListener {
            val intent = Intent(this, RedeemActivity::class.java)
            startActivity(intent)
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_gift // or nav_menu / nav_order depending on the screen

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.nav_gift -> {
                    // Already on GiftActivity
                    true
                }
                R.id.nav_order -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun updateUserInfo(fullName: String, drinkCount: Int, points: Int) {
        val countTextView = findViewById<TextView>(R.id.countText)
        val cupContainer = findViewById<LinearLayout>(R.id.cupContainer)
        val pointsValueTextView = findViewById<TextView>(R.id.pointsValue)

        val count = drinkCount.coerceIn(0, 8)
        countTextView.text = "$count/8"
        pointsValueTextView.text = points.toString()

        // Clear and add cups
        cupContainer.removeAllViews()
        for (i in 1..8) {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(30.dp, 30.dp).apply {
                marginEnd = 4.dp
            }
            imageView.layoutParams = layoutParams

            val drawableId = if (i <= count) R.drawable.iced_cup else R.drawable.empty_cup
            imageView.setImageResource(drawableId)

            cupContainer.addView(imageView)
        }
    }
    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}