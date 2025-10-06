package com.example.onlineshoppingsystem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingsystem.databinding.ItemCartBinding

class CartAdapter(val items: MutableList<CartItem>, val onRemove: (CartItem) -> Unit, val onQuantityChange: (CartItem, Int) -> Unit)
    : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvName.text = item.name
        holder.b.tvPrice.text = "Price: $${item.price}"
        holder.b.tvQuantity.text = item.quantity.toString()

        holder.b.btnIncrease.setOnClickListener {
            onQuantityChange(item, item.quantity + 1)
        }

        holder.b.btnDecrease.setOnClickListener {
            if (item.quantity > 1) onQuantityChange(item, item.quantity - 1)
        }

        holder.b.btnRemove.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount() = items.size
}
