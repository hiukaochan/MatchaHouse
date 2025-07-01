package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import com.example.thecodecup.data.CartItem
import com.example.thecodecup.data.AppDatabase
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
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
import com.example.thecodecup.data.Drink

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        val cartIcon = findViewById<ImageView>(R.id.cart)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Handle system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
        val backButton = findViewById<ImageView>(R.id.back)
        val drinkName = intent.getStringExtra("drinkName") ?: "Unknown"
        val drinkPrice = intent.getIntExtra("drinkPrice", 0)
        val drawableName = intent.getStringExtra("drawableName") ?: "placeholder"
        val drinkId = intent.getIntExtra("drinkId", 0)
        val qtyText = findViewById<TextView>(R.id.qty)
        val plus = findViewById<TextView>(R.id.plus)
        val minus = findViewById<TextView>(R.id.minus)

// Set drink name and image
        val drinkNameTextView = findViewById<TextView>(R.id.drinkName)
        val drinkImageView = findViewById<ImageView>(R.id.drinkImage)
        val totalText = findViewById<TextView>(R.id.totalNum)

        drinkNameTextView.text = drinkName
        val imageResId = resources.getIdentifier(drawableName, "drawable", packageName)
        drinkImageView.setImageResource(imageResId)
        totalText.text = "${drinkPrice} VND"

        var quantity = 1
        qtyText.text = quantity.toString()

        plus.setOnClickListener {
            quantity++
            qtyText.text = quantity.toString()
            updateTotalPrice(quantity)
        }

        minus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                qtyText.text = quantity.toString()
                updateTotalPrice(quantity)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        val lsweet = findViewById<TextView>(R.id.lsweetName)
        val rsweet = findViewById<TextView>(R.id.rsweetName)
// Milk
        val meiji = findViewById<TextView>(R.id.meijiName)
        val oat = findViewById<TextView>(R.id.oatName)
// Ice
        val lice = findViewById<TextView>(R.id.liceName)
        val rice = findViewById<TextView>(R.id.riceName)

// Selection state
        var sweetness = "70%"
        var milkType = "Meiji"
        var iceLevel = "100%"

        // Default selected buttons
        rsweet.isSelected = true
        lsweet.isSelected = false

        meiji.isSelected = true
        oat.isSelected = false

        rice.isSelected = true
        lice.isSelected = false


        // Sweetness
        lsweet.setOnClickListener {
            lsweet.isSelected = true
            rsweet.isSelected = false
            sweetness = "30%"
        }

        rsweet.setOnClickListener {
            rsweet.isSelected = true
            lsweet.isSelected = false
            sweetness = "70%"
        }

// Milk
        meiji.setOnClickListener {
            meiji.isSelected = true
            oat.isSelected = false
            milkType = "Meiji"
        }

        oat.setOnClickListener {
            oat.isSelected = true
            meiji.isSelected = false
            milkType = "Oatside"
        }

// Ice
        lice.setOnClickListener {
            lice.isSelected = true
            rice.isSelected = false
            iceLevel = "50%"
        }

        rice.setOnClickListener {
            rice.isSelected = true
            lice.isSelected = false
            iceLevel = "100%"
        }

        val addToCartButton = findViewById<TextView>(R.id.addcartbutton)

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
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "coffee_shop_db"
                ).build()

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
            }

        }


    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
    fun updateTotalPrice(quantity: Int) {
        val price = intent.getIntExtra("drinkPrice", 0)
        val total = quantity * price
        findViewById<TextView>(R.id.totalNum).text = "${total} VND"
    }

}
