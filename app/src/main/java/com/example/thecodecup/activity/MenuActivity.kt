package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.thecodecup.R
import com.example.thecodecup.data.drinkList
import com.example.thecodecup.data.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val profileResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val userProfile = db.userProfileDao().getProfile()
                userProfile?.let { profile ->
                    findViewById<TextView>(R.id.userName).text = profile.fullName.uppercase()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        // ✅ Initialize the class-level db, not shadow it
        db = AppDatabase.getInstance(this)

        val userNameTextView = findViewById<TextView>(R.id.userName)

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
                userNameTextView.text = userProfile.fullName.uppercase()
            }
        }


        val drinkGrid = findViewById<GridLayout>(R.id.drinkGrid)

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }
}