package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingsystem.databinding.ItemCategoryCustomerBinding

class CustomerCategoryAdapter(
    private val items: MutableList<Category>,
    private val onOpenProducts: (Category) -> Unit
) : RecyclerView.Adapter<CustomerCategoryAdapter.VH>() {

    inner class VH(val b: ItemCategoryCustomerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCategoryCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvCategoryName.text = item.name

        // فك ترميز صورة Base64 لو موجودة
        if (item.imageBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(item.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.b.imgCategory.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.b.imgCategory.setImageResource(R.drawable.ic_launcher_foreground) // fallback
            }
        } else {
            holder.b.imgCategory.setImageResource(R.drawable.ic_launcher_foreground) // fallback
        }

        // لما يضغط الزبون على الكارد
        holder.itemView.setOnClickListener { onOpenProducts(item) }
    }

    override fun getItemCount() = items.size
}
