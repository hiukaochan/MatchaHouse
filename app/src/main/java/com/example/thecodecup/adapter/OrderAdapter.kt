package com.example.thecodecup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.OrderItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OrderAdapter(
    private val orders: MutableList<OrderItem>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkName: TextView = view.findViewById(R.id.orderDrinkName)
        val address: TextView = view.findViewById(R.id.orderAddress)
        val price: TextView = view.findViewById(R.id.totalAmount)
        val date: TextView = view.findViewById(R.id.orderDate)
        val icon: ImageView = view.findViewById(R.id.orderIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.drinkName.text = order.drinkName
        holder.address.text = order.address
        holder.price.text = "${"%.0f".format(order.totalAmount)}"
        holder.date.text = SimpleDateFormat("dd MMMM | HH:mm a", Locale.getDefault()).format(Date(order.timestamp))
//        holder.icon.setImageResource(R.drawable.matchaicon) // change as needed
    }

    fun getItemAt(position: Int) = orders[position]

    fun removeAt(position: Int) {
        orders.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateList(newOrders: List<OrderItem>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}
