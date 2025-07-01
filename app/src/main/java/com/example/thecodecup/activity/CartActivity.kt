package com.example.thecodecup.activity
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.flow.first

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.thecodecup.R
import android.content.Intent
import android.widget.Button
import android.widget.ImageView

import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.adapter.CartItemAdapter
import androidx.room.Room
import com.example.thecodecup.data.AppDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch


class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

            val checkoutButton = findViewById<Button>(R.id.checkoutButton)
            checkoutButton.setOnClickListener {
                val intent = Intent(this, OrderCompleteActivity::class.java)
                startActivity(intent)
            }

        val recyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "coffee_shop_db"
        ).build()

        lifecycleScope.launch {
            val cartItems = db.cartDao().getAllCartItems().first().toMutableList()
            val adapter = CartItemAdapter(cartItems)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@CartActivity)

            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.getItemAt(position)

                    lifecycleScope.launch {
                        db.cartDao().deleteCartItem(item)
                        adapter.removeAt(position)
                        updateTotalAmount(db)
                    }
                }
            })
            itemTouchHelper.attachToRecyclerView(recyclerView)

            updateTotalAmount(db)
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun updateTotalAmount(db: AppDatabase) {
        lifecycleScope.launch {
            val total = db.cartDao().getCartTotal().first() ?: 0.0
            val totalAmountTextView = findViewById<TextView>(R.id.totalAmountNum)
            totalAmountTextView.text = "${"%.0f".format(total)} VND"
        }
    }
}