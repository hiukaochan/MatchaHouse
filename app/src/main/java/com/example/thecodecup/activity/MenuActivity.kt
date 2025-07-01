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
import com.example.thecodecup.data.Drink

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val drinkGrid = findViewById<GridLayout>(R.id.drinkGrid)

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
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