package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshoppingsystem.databinding.ActivityProductListBinding
import com.google.firebase.database.*

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductAdapterAdmin
    private lateinit var dbRef: DatabaseReference
    private val products = mutableListOf<Product>()

    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // استقبل البيانات من CategoryListActivity
        categoryId = intent.getStringExtra("categoryId")
        categoryName = intent.getStringExtra("categoryName")
        binding.tvCategoryName.text = "Category: $categoryName"


        //  عرض اسم التصنيف في التولبار
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = categoryName ?: "Products"

        dbRef = FirebaseDatabase.getInstance().getReference("products")

        // زر إضافة منتج جديد (FloatingActionButton)
        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, ProductFormActivity::class.java)
            intent.putExtra("categoryId", categoryId)
            startActivity(intent)
        }

        // RecyclerView
        adapter = ProductAdapterAdmin(
            items = products,
            onEdit = { product ->
                val intent = Intent(this, ProductFormActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onDelete = { product ->
                dbRef.child(product.id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                    }
            }
        )

        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter

        loadProducts()
    }

    private fun loadProducts() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    if (product != null && product.categoryId == categoryId) {
                        products.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductListActivity, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
