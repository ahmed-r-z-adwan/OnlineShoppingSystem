package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineshoppingsystem.databinding.ActivityCustomerCategoryBinding
import com.google.firebase.database.*

class CustomerCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerCategoryBinding
    private lateinit var adapter: CustomerCategoryAdapter
    private val categories = mutableListOf<Category>()

    private val ref: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("categories")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView Grid
        adapter = CustomerCategoryAdapter(categories) { category ->
            // لما يضغط الزبون على كاتيجوري → نوديه لصفحة المنتجات
            val i = Intent(this, CustomerProductListActivity::class.java)
            i.putExtra("categoryId", category.id)
            i.putExtra("categoryName", category.name)
            startActivity(i)
        }

        binding.recyclerCategories.layoutManager = GridLayoutManager(this, 2) // 2 أعمدة
        binding.recyclerCategories.adapter = adapter

        //  زر الكارت
        binding.btnCart.setOnClickListener {
            val i = Intent(this, CartActivity::class.java)
            startActivity(i)
        }

        loadCategories()
        loadCategoriess()
    }

    private fun loadCategories() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (ch in snapshot.children) {
                    ch.getValue(Category::class.java)?.let { categories.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerCategoryActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun loadCategoriess() {
        binding.progressBar.visibility = View.VISIBLE  //  أظهر الـ progressbar

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (ch in snapshot.children) {
                    ch.getValue(Category::class.java)?.let { categories.add(it) }
                }
                adapter.notifyDataSetChanged()

                binding.progressBar.visibility = View.GONE  //  أخفيه بعد ما تجهز البيانات
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerCategoryActivity, error.message, Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE  //  برضو نخفيه لو صار خطأ
            }
        })
    }

}
