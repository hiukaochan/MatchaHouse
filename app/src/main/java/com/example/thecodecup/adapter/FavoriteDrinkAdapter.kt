package com.example.thecodecup.adapter

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.FavoriteDrink
import kotlin.collections.get

class FavoriteDrinkAdapter(private var items: List<FavoriteDrink>) :
    RecyclerView.Adapter<FavoriteDrinkAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkImage: ImageView = view.findViewById(R.id.favImage)
        val drinkName: TextView = view.findViewById(R.id.favName)
        val drinkOptions: TextView = view.findViewById(R.id.favDes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drink = items[position]
        val resId = holder.itemView.context.resources.getIdentifier(
            drink.drinkImage, "drawable", holder.itemView.context.packageName
        )
        holder.drinkImage.setImageResource(resId)
        holder.drinkName.text = drink.drinkName
        holder.drinkOptions.text =
            "${drink.sweetness} sweetness | ${drink.milkType} | ${drink.iceLevel} ice"
    }

    fun updateData(newList: List<FavoriteDrink>) {
        items = newList
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): FavoriteDrink = items[position]
}
