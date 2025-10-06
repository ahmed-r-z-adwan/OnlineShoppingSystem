package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingsystem.databinding.ItemProductCustomerBinding

class CustomerProductAdapter(
    private val items: MutableList<Product>,
    private val onOpenDetails: (Product) -> Unit
) : RecyclerView.Adapter<CustomerProductAdapter.VH>() {

    inner class VH(val b: ItemProductCustomerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.b.tvName.text = item.name
        holder.b.tvPrice.text = "$${item.price}"

        // فك الصورة من Base64
        if (item.imageBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(item.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.b.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.b.imgProduct.setImageResource(R.drawable.ic_launcher_foreground) // fallback
            }
        } else {
            holder.b.imgProduct.setImageResource(R.drawable.ic_launcher_foreground) // fallback
        }

        // لما يضغط الزبون على المنتج
        holder.itemView.setOnClickListener { onOpenDetails(item) }
    }

    override fun getItemCount() = items.size
}
