package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import com.example.thecodecup.data.drinkList
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    // Activity result for profile completion
    private val profileResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val userProfile = db.userProfileDao().getProfile()
                userProfile?.let { profile ->
                    updateUserInfo(profile.fullName, profile.drinkCount)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        db = AppDatabase.getInstance(this)

        val drinkGrid = findViewById<GridLayout>(R.id.drinkGrid)
        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        profileIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Load user data and loyalty cups
        lifecycleScope.launch {
            val userProfile = db.userProfileDao().getProfile()
            if (userProfile == null) {
                Toast.makeText(
                    this@MenuActivity,
                    "Please complete your profile before ordering.",
                    Toast.LENGTH_LONG
                ).show()

                kotlinx.coroutines.delay(1000)
                profileResultLauncher.launch(Intent(this@MenuActivity, ProfileActivity::class.java))
            } else {
                updateUserInfo(userProfile.fullName, userProfile.drinkCount)
            }
        }

        // Load all drinks
        for (drink in drinkList) {
            val cardView = layoutInflater.inflate(R.layout.item_card, drinkGrid, false)

            val drinkImage = cardView.findViewById<ImageView>(R.id.matchaImage)
            val drinkName = cardView.findViewById<TextView>(R.id.matchaName)

            val resId = resources.getIdentifier(drink.drawableName, "drawable", packageName)
            drinkImage.setImageResource(resId)
            drinkName.text = drink.name

            cardView.setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("drinkName", drink.name)
                intent.putExtra("drinkPrice", drink.price)
                intent.putExtra("drawableName", drink.drawableName)
                intent.putExtra("drinkId", drink.id)
                startActivity(intent)
            }

            drinkGrid.addView(cardView)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_menu // Or nav_menu / nav_order based on activity

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_gift -> {
                    startActivity(Intent(this, GiftActivity::class.java))
                    true
                }
                R.id.nav_menu -> true
                R.id.nav_order -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                    true
                }
                else -> false
            }
        }


        // Window Insets
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

    private fun updateUserInfo(fullName: String, drinkCount: Int) {
        val userNameTextView = findViewById<TextView>(R.id.userName)
        val countTextView = findViewById<TextView>(R.id.countText)
        val cupContainer = findViewById<LinearLayout>(R.id.cupContainer)

        userNameTextView.text = fullName.uppercase()
        val count = drinkCount.coerceIn(0, 8)
        countTextView.text = "$count/8"

        // Reset and add cups dynamically
        cupContainer.removeAllViews()
        for (i in 1..8) {
            val imageView = ImageView(this@MenuActivity)
            val layoutParams = LinearLayout.LayoutParams(30.dp, 30.dp).apply {
                marginEnd = 4.dp
            }
            imageView.layoutParams = layoutParams

            val drawableId = if (i <= count) R.drawable.iced_cup else R.drawable.empty_cup
            imageView.setImageResource(drawableId)

            cupContainer.addView(imageView)
        }
    }

    // Extension to convert dp to pixels
    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
