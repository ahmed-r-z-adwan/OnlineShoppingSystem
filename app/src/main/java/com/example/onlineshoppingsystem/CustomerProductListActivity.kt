package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshoppingsystem.databinding.ActivityCustomerProductListBinding
import com.google.firebase.database.*

class CustomerProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerProductListBinding
    private lateinit var adapter: CustomerProductAdapter
    private lateinit var dbRef: DatabaseReference

    private val products = mutableListOf<Product>()
    private val filteredProducts = mutableListOf<Product>()

    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // استلام بيانات التصنيف
        categoryId = intent.getStringExtra("categoryId")
        categoryName = intent.getStringExtra("categoryName")
        title = "Category: $categoryName"

        dbRef = FirebaseDatabase.getInstance().getReference("products")

        // إعداد RecyclerView
        adapter = CustomerProductAdapter(filteredProducts) { product ->
            val intent = Intent(this, CustomerProductDetailsActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
        binding.recyclerCustomerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerCustomerProducts.adapter = adapter

        // البحث
        binding.etSearch.addTextChangedListener { text ->
            filterProducts(text.toString())
        }

        // تحميل المنتجات
        loadProducts()
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE  //  قبل ما يبدأ التحميل

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    if (product != null && product.categoryId == categoryId) {
                        products.add(product)
                    }
                }

                // تحديث القائمة حسب البحث الحالي
                filterProducts(binding.etSearch.text.toString())

                binding.progressBar.visibility = View.GONE  //  بعد ما يخلص
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CustomerProductListActivity,
                    "Failed: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun filterProducts(query: String) {
        filteredProducts.clear()
        if (query.isEmpty()) {
            filteredProducts.addAll(products)
        } else {
            filteredProducts.addAll(
                products.filter { it.name.contains(query, ignoreCase = true) }
            )
        }
        adapter.notifyDataSetChanged()
    }
}
