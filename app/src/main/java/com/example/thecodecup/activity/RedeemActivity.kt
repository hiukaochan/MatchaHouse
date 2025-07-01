package com.example.thecodecup.activity

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import com.example.thecodecup.data.drinkList
import kotlin.math.max
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RedeemActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var currentPoints: Int = 0
    private lateinit var pointsRecyclerView: RecyclerView
    private lateinit var redeemAdapter: RedeemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_redeem)

        db = AppDatabase.getInstance(this)
        pointsRecyclerView = findViewById(R.id.pointsRecyclerView)
        pointsRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val profile = db.userProfileDao().getProfile()
            if (profile != null) {
                currentPoints = profile.points
                setupRecycler()
            }
        }

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }

    private fun setupRecycler() {
        redeemAdapter = RedeemAdapter(drinkList, ::getCurrentPoints) { drink ->
            val cost = (drink.price * 2 / 100)
            if (currentPoints >= cost) {
                currentPoints -= cost
                lifecycleScope.launch {
                    val user = db.userProfileDao().getProfile()
                    if (user != null) {
                        // 1. Update user profile with new points
                        val updated = user.copy(points = max(0, currentPoints))
                        db.userProfileDao().upsertProfile(updated)

                        // 2. Log to points history as a negative entry
                        db.pointsHistoryDao().insert(
                            com.example.thecodecup.data.PointsHistory(
                                drinkName = drink.name,
                                pointsEarned = -cost,
                                timestamp = System.currentTimeMillis()
                            )
                        )

                        Toast.makeText(
                            this@RedeemActivity,
                            "Redeemed ${drink.name} for $cost points",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@RedeemActivity,
                    "Not enough points!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        pointsRecyclerView.adapter = redeemAdapter
    }


    private fun getCurrentPoints(): Int = currentPoints
}