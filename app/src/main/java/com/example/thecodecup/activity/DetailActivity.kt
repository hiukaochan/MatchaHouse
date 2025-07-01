package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.thecodecup.R
import com.example.thecodecup.data.AppDatabase
import com.example.thecodecup.data.CartItem
import com.example.thecodecup.data.FavoriteDrink
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var heartIcon: ImageView
    private var isFavorite = false

    private lateinit var drinkName: String
    private var drinkPrice: Int = 0
    private lateinit var drawableName: String

    private var sweetness = "70%"
    private var milkType = "Meiji"
    private var iceLevel = "100%"
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        db = AppDatabase.getInstance(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

        // Intent data
        drinkName = intent.getStringExtra("drinkName") ?: "Unknown"
        drinkPrice = intent.getIntExtra("drinkPrice", 0)
        drawableName = intent.getStringExtra("drawableName") ?: "placeholder"

        val drinkNameTextView = findViewById<TextView>(R.id.drinkName)
        val drinkImageView = findViewById<ImageView>(R.id.drinkImage)
        val totalText = findViewById<TextView>(R.id.totalNum)
        val qtyText = findViewById<TextView>(R.id.qty)
        val plus = findViewById<TextView>(R.id.plus)
        val minus = findViewById<TextView>(R.id.minus)
        val addToCartButton = findViewById<TextView>(R.id.addcartbutton)
        heartIcon = findViewById(R.id.favIcon)
        val cartIcon = findViewById<ImageView>(R.id.cart)
        val backButton = findViewById<ImageView>(R.id.back)

        // UI Setup
        drinkNameTextView.text = drinkName
        drinkImageView.setImageResource(resources.getIdentifier(drawableName, "drawable", packageName))
        qtyText.text = quantity.toString()
        totalText.text = "$drinkPrice VND"

        plus.setOnClickListener {
            quantity++
            qtyText.text = quantity.toString()
            updateTotalPrice()
        }

        minus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                qtyText.text = quantity.toString()
                updateTotalPrice()
            }
        }

        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        backButton.setOnClickListener {
            finish()
        }

        // Selection buttons
        val lsweet = findViewById<TextView>(R.id.lsweetName)
        val rsweet = findViewById<TextView>(R.id.rsweetName)
        val meiji = findViewById<TextView>(R.id.meijiName)
        val oat = findViewById<TextView>(R.id.oatName)
        val lice = findViewById<TextView>(R.id.liceName)
        val rice = findViewById<TextView>(R.id.riceName)

        // Initial selection
        rsweet.isSelected = true
        meiji.isSelected = true
        rice.isSelected = true

        // Sweetness
        lsweet.setOnClickListener {
            sweetness = "30%"
            lsweet.isSelected = true
            rsweet.isSelected = false
            updateHeartIconFromDb()
        }

        rsweet.setOnClickListener {
            sweetness = "70%"
            rsweet.isSelected = true
            lsweet.isSelected = false
            updateHeartIconFromDb()
        }

        // Milk
        meiji.setOnClickListener {
            milkType = "Meiji"
            meiji.isSelected = true
            oat.isSelected = false
            updateHeartIconFromDb()
        }

        oat.setOnClickListener {
            milkType = "Oatside"
            oat.isSelected = true
            meiji.isSelected = false
            updateHeartIconFromDb()
        }

        // Ice
        lice.setOnClickListener {
            iceLevel = "50%"
            lice.isSelected = true
            rice.isSelected = false
            updateHeartIconFromDb()
        }

        rice.setOnClickListener {
            iceLevel = "100%"
            rice.isSelected = true
            lice.isSelected = false
            updateHeartIconFromDb()
        }

        // Add to Cart
        addToCartButton.setOnClickListener {
            val newItem = CartItem(
                drinkName = drinkName,
                drinkImage = drawableName,
                sweetness = sweetness,
                milkType = milkType,
                iceLevel = iceLevel,
                quantity = quantity,
                totalAmount = quantity * drinkPrice.toDouble()
            )

            lifecycleScope.launch {
                val existingItem = db.cartDao().findMatchingCartItem(
                    newItem.drinkName,
                    newItem.drinkImage,
                    newItem.sweetness,
                    newItem.milkType,
                    newItem.iceLevel
                )

                if (existingItem != null) {
                    val updatedItem = existingItem.copy(
                        quantity = existingItem.quantity + newItem.quantity,
                        totalAmount = existingItem.totalAmount + newItem.totalAmount
                    )
                    db.cartDao().updateCartItem(updatedItem)
                } else {
                    db.cartDao().insertCartItem(newItem)
                }

                Toast.makeText(this@DetailActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@DetailActivity, CartActivity::class.java))
            }
        }

        // Favorite icon toggle
        lifecycleScope.launch {
            updateHeartIconFromDb()
        }

        heartIcon.setOnClickListener {
            lifecycleScope.launch {
                val favorite = FavoriteDrink(drinkName, drawableName, sweetness, milkType, iceLevel)
                if (isFavorite) {
                    db.favoriteDrinkDao().deleteFavorite(favorite)
                    isFavorite = false
                } else {
                    db.favoriteDrinkDao().insertFavorite(favorite)
                    isFavorite = true
                }
                updateHeartIcon()
            }
        }
    }

    private fun updateTotalPrice() {
        val total = quantity * drinkPrice
        findViewById<TextView>(R.id.totalNum).text = "$total VND"
    }

    private fun updateHeartIconFromDb() {
        lifecycleScope.launch {
            val favorite = db.favoriteDrinkDao().getFavorite(drinkName, sweetness, milkType, iceLevel)
            isFavorite = favorite != null
            updateHeartIcon()
        }
    }

    private fun updateHeartIcon() {
        heartIcon.setImageResource(if (isFavorite) R.drawable.fav_filled else R.drawable.fav)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
