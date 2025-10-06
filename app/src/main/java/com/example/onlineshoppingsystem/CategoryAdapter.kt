package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshoppingsystem.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val items: MutableList<Category>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit,
    private val onOpenProducts: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    inner class VH(val b: ItemCategoryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvCatName.text = item.name

        //  فك ترميز صورة الكاتيجوري لو موجودة
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

        // أزرار التعديل والحذف
        holder.b.btnEdit.setOnClickListener { onEdit(item) }
        holder.b.btnDelete.setOnClickListener { onDelete(item) }

        //  افتح المنتجات
        holder.itemView.setOnClickListener { onOpenProducts(item) }
    }

    override fun getItemCount() = items.size
}
