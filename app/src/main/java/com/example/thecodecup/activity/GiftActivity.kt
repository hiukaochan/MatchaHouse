package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thecodecup.adapter.PointsHistoryAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GiftActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: PointsHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gift)

        db = AppDatabase.getInstance(this)

        // Setup RecyclerView
        historyRecyclerView = findViewById(R.id.pointsRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = PointsHistoryAdapter(mutableListOf())
        historyRecyclerView.adapter = historyAdapter

        val redeemButton = findViewById<Button>(R.id.redeemButton)
        redeemButton.setOnClickListener {
            val intent = Intent(this, RedeemActivity::class.java)
            startActivity(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_gift
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.nav_gift -> true
                R.id.nav_order -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                    true
                }
                else -> false
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav)) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userProfile = db.userProfileDao().getProfile()
            if (userProfile != null) {
                updateUserInfo(userProfile.fullName, userProfile.drinkCount, userProfile.points)
            }
            db.pointsHistoryDao().getAll().collect { history ->
                historyAdapter.updateData(history)
            }
        }
    }


    private fun updateUserInfo(fullName: String, drinkCount: Int, points: Int) {
        val countTextView = findViewById<TextView>(R.id.countText)
        val cupContainer = findViewById<LinearLayout>(R.id.cupContainer)
        val pointsValueTextView = findViewById<TextView>(R.id.pointsValue)

        val stampCount = drinkCount % 8
        countTextView.text = "$drinkCount/8"
        pointsValueTextView.text = points.toString()

        cupContainer.removeAllViews()
        for (i in 1..8) {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(30.dp, 30.dp).apply {
                marginEnd = 4.dp
            }
            imageView.layoutParams = layoutParams

            val drawableId = if (i <= stampCount) R.drawable.iced_cup else R.drawable.empty_cup
            imageView.setImageResource(drawableId)

            cupContainer.addView(imageView)
        }

        // 💡 Add this click listener to allow manual redemption
        cupContainer.setOnClickListener {
            if (drinkCount >= 8) {
                lifecycleScope.launch {
                    val userProfile = db.userProfileDao().getProfile()
                    userProfile?.let {
                        val newCount = it.drinkCount - 8
                        db.userProfileDao().upsertProfile(it.copy(drinkCount = newCount))
                        runOnUiThread {
                            Toast.makeText(
                                this@GiftActivity,
                                "8 stamps redeemed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUserInfo(it.fullName, newCount, it.points)
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this@GiftActivity,
                    "You need at least 8 stamps to redeem a reward.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
