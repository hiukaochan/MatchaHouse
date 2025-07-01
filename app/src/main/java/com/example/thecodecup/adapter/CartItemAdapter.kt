package com.example.thecodecup.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thecodecup.R
import com.example.thecodecup.data.CartItem


class CartItemAdapter(
    private var items: MutableList<CartItem>
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drinkImage = itemView.findViewById<ImageView>(R.id.cartImage)
        val drinkName = itemView.findViewById<TextView>(R.id.cartDrinkName)
        val description = itemView.findViewById<TextView>(R.id.cartDescription)
        val quantity = itemView.findViewById<TextView>(R.id.cartQuantity)
        val total = itemView.findViewById<TextView>(R.id.cartTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.drinkName.text = item.drinkName
        holder.description.text = "${item.sweetness} sweet | ${item.milkType} | ${item.iceLevel} ice"
        holder.quantity.text = "x${item.quantity}"
        holder.total.text = "${"%.0f".format(item.totalAmount)}"
        val resId = holder.itemView.resources.getIdentifier(
            item.drinkImage, "drawable", holder.itemView.context.packageName)
        holder.drinkImage.setImageResource(resId)
    }

    override fun getItemCount(): Int = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItemAt(position: Int): CartItem = items[position]
}
