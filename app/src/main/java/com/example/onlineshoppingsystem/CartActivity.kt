package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshoppingsystem.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var dbRef: DatabaseReference
    private val cartItems = mutableListOf<CartItem>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = auth.currentUser?.uid ?: "guest"
        dbRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        // RecyclerView setup
        adapter = CartAdapter(
            cartItems,
            onRemove = { item -> removeFromCart(item) },
            onQuantityChange = { item, newQty -> updateQuantity(item, newQty) }
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerCart.adapter = adapter

        loadCart()

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadCart() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (child in snapshot.children) {
                    val cartItem = child.getValue(CartItem::class.java)
                    if (cartItem != null) cartItems.add(cartItem)
                }
                adapter.notifyDataSetChanged()

                val total = cartItems.sumOf { it.price * it.quantity }
                binding.tvTotal.text = "Total: $${String.format("%.2f", total)}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeFromCart(item: CartItem) {
        dbRef.child(item.productId).removeValue()
    }

    private fun updateQuantity(item: CartItem, newQty: Int) {
        dbRef.child(item.productId).child("quantity").setValue(newQty)
    }
}
