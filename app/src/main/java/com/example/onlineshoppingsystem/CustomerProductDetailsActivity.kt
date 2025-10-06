package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityCustomerProductDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CustomerProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerProductDetailsBinding
    private var product: Product? = null
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("carts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getSerializableExtra("product") as? Product
        product?.let { showProduct(it) }

        binding.btnAddToCart.setOnClickListener {
            product?.let { addToCart(it) }
        }
    }

    private fun showProduct(product: Product) {
        binding.tvName.text = product.name
        binding.tvDesc.text = product.description
        binding.tvPrice.text = "Price: $${product.price}"
        binding.tvLocation.text = "Location: ${product.location}"
        binding.tvRate.text = "Rate: ${product.rate}/5"

        if (product.imageBase64.isNotEmpty()) {
            val bytes = Base64.decode(product.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imgProduct.setImageBitmap(bitmap)
        }
    }

    private fun addToCart(product: Product) {
        val userId = auth.currentUser?.uid ?: "guest"  //  افتراضياً لو مش عامل تسجيل
        val cartItem = CartItem(
            productId = product.id,
            userId = userId,
            name = product.name,
            price = product.price,
            quantity = 1
        )

        dbRef.child(userId).child(product.id).setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }
}
