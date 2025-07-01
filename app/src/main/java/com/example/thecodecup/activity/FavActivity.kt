package com.example.thecodecup.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.example.thecodecup.R
import com.example.thecodecup.adapter.FavoriteDrinkAdapter
import com.example.thecodecup.data.AppDatabase
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.thecodecup.data.FavoriteDrink
import kotlinx.coroutines.launch

class FavActivity : AppCompatActivity() {
    private lateinit var adapter: FavoriteDrinkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.favRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FavoriteDrinkAdapter(emptyList())
        recyclerView.adapter = adapter

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            db.favoriteDrinkDao().getAllFavorites().collect { favorites ->
                adapter.updateData(favorites)
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favorite = adapter.getItemAt(position)

                lifecycleScope.launch {
                    db.favoriteDrinkDao().deleteFavorite(favorite)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

}
