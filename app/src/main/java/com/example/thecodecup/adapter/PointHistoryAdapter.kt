package com.example.thecodecup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.PointsHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PointsHistoryAdapter(private val items: MutableList<PointsHistory>) :
    RecyclerView.Adapter<PointsHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkNameText: TextView = view.findViewById(R.id.pointDrinkName)
        val pointsText: TextView = view.findViewById(R.id.totalPoint)
        val dateText: TextView = view.findViewById(R.id.pointDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_point, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.drinkNameText.text = item.drinkName
        holder.pointsText.text = if (item.pointsEarned >= 0) "+${item.pointsEarned} pts" else "${item.pointsEarned} pts"

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.dateText.text = sdf.format(Date(item.timestamp))
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<PointsHistory>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
