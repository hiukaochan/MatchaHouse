package com.example.thecodecup.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.Drink

class RedeemAdapter(
    private val drinks: List<Drink>,
    private val getPoints: () -> Int,  // fetch fresh value on each bind
    private val onRedeemClick: (Drink) -> Unit
) : RecyclerView.Adapter<RedeemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drinkImage: ImageView = itemView.findViewById(R.id.drinkImage)
        val drinkName: TextView = itemView.findViewById(R.id.drinkName)
        val drinkDate: TextView = itemView.findViewById(R.id.drinkDate)
        val redeemButton: Button = itemView.findViewById(R.id.redeemPoint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = drinks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drink = drinks[position]
        val cost = (drink.price * 2 / 100)  // Corrected points cost formula

        val resId = holder.itemView.context.resources.getIdentifier(
            drink.drawableName, "drawable", holder.itemView.context.packageName
        )
        holder.drinkImage.setImageResource(resId)
        holder.drinkName.text = drink.name
        holder.drinkDate.text = "Valid Until: 01 Jul 2026"
        holder.redeemButton.text = "$cost pts"

        val userPoints = getPoints()
        holder.redeemButton.isEnabled = userPoints >= cost

        holder.redeemButton.setOnClickListener {
            onRedeemClick(drink)
            notifyItemChanged(position) // Refresh after redeem
        }
    }
}
