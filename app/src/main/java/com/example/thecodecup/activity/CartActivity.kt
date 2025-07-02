package com.example.thecodecup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.thecodecup.R
import com.example.thecodecup.adapter.CartItemAdapter
import com.example.thecodecup.data.AppDatabase
import com.example.thecodecup.data.OrderItem
import com.example.thecodecup.data.PointsHistory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "coffee_shop_db"
        ).build()

        val checkoutButton = findViewById<Button>(R.id.checkoutButton)
        val recyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)

        // Checkout logic
        checkoutButton.setOnClickListener {
            lifecycleScope.launch {
                val cartItems = db.cartDao().getAllCartItems().first()
                val userProfile = db.userProfileDao().getProfile()

                if (cartItems.isNotEmpty() && userProfile != null) {
                    var drinkCount = userProfile.drinkCount
                    var loyaltyPoints = userProfile.points

                    for (cartItem in cartItems) {
                        // Insert order
                        val orderItem = OrderItem(
                            drinkName = cartItem.drinkName,
                            drinkImage = cartItem.drinkImage,
                            sweetness = cartItem.sweetness,
                            milkType = cartItem.milkType,
                            iceLevel = cartItem.iceLevel,
                            quantity = cartItem.quantity,
                            totalAmount = cartItem.totalAmount,
                            address = userProfile.address,
                            status = "ongoing"
                        )
                        db.orderDao().insertOrderItem(orderItem)

                        // Loyalty calculation
                        drinkCount += cartItem.quantity
                        loyaltyPoints += (cartItem.totalAmount / 1000).toInt()

                        // Add to points history
                        val history = PointsHistory(
                            drinkName = cartItem.drinkName,
                            pointsEarned = (cartItem.totalAmount / 1000).toInt()
                        )
                        db.pointsHistoryDao().insert(history)
                    }

//                    drinkCount %= 8

                    val updatedProfile = userProfile.copy(
                        drinkCount = drinkCount,
                        points = loyaltyPoints
                    )
                    db.userProfileDao().upsertProfile(updatedProfile)

                    db.cartDao().clearCart()

                    // Navigate to order complete
                    startActivity(Intent(this@CartActivity, OrderCompleteActivity::class.java))
                    finish()
                }
            }
        }
        val backButton = findViewById<ImageView>(R.id.back)
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Swipe to delete
        lifecycleScope.launch {
            val cartItems = db.cartDao().getAllCartItems().first().toMutableList()
            val adapter = CartItemAdapter(cartItems)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@CartActivity)

            val itemTouchHelper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
                        updateTotalAmount()
                    }
                }
            })
            itemTouchHelper.attachToRecyclerView(recyclerView)

            updateTotalAmount()
        }

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }

    private fun updateTotalAmount() {
        lifecycleScope.launch {
            val total = db.cartDao().getCartTotal().first() ?: 0.0
            val totalAmountTextView = findViewById<TextView>(R.id.totalAmountNum)
            totalAmountTextView.text = "${"%.0f".format(total)} VND"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
