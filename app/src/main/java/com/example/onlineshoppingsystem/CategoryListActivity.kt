package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshoppingsystem.databinding.ActivityCategoryListBinding
import com.google.firebase.database.*

class CategoryListActivity : AppCompatActivity() {

    private lateinit var b: ActivityCategoryListBinding
    private val categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter

    private val ref: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("categories")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(b.root)

        // RecyclerView setup
        b.recycler.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(
            categories,
            onEdit = { cat -> openForm(cat) },
            onDelete = { cat -> confirmDelete(cat) },
            onOpenProducts = { cat -> openProducts(cat) }   // جديد
        )
        b.recycler.adapter = adapter

        // زر الإضافة
        b.fabAdd.setOnClickListener {
            openForm(null)
        }

        listenLive()
    }

    private fun listenLive() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (ch in snapshot.children) {
                    ch.getValue(Category::class.java)?.let { categories.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CategoryListActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openForm(cat: Category?) {
        val i = Intent(this, CategoryFormActivity::class.java)
        if (cat != null) {
            i.putExtra("catId", cat.id)
            i.putExtra("catName", cat.name)
        }
        startActivity(i)
    }

    private fun confirmDelete(cat: Category) {
        AlertDialog.Builder(this)
            .setMessage("Delete '${cat.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                ref.child(cat.id).removeValue()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //  فتح منتجات التصنيف
    private fun openProducts(cat: Category) {
        val i = Intent(this, ProductListActivity::class.java)
        i.putExtra("categoryId", cat.id)
        i.putExtra("categoryName", cat.name)
        startActivity(i)
    }
}
