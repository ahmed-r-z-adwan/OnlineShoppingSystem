package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingsystem.databinding.ItemProductAdminBinding

class ProductAdapterAdmin(
    private val items: MutableList<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapterAdmin.VH>() {

    inner class VH(val b: ItemProductAdminBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.b.tvName.text = item.name
        holder.b.tvPrice.text = "$${item.price}"

        // فك ترميز Base64 وتحويله لصورة
        if (item.imageBase64.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(item.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.b.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.b.imgProduct.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            holder.b.imgProduct.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.b.btnEdit.setOnClickListener { onEdit(item) }
        holder.b.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = items.size
}
