package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // لو جبت الإجمالي من CartActivity
        val intentTotal = intent.getDoubleExtra("totalAmount", 0.0)

        // كخطوة أمان: لو ما وصل من CartActivity، رح نجيب المجموع من Firebase
        if (intentTotal > 0.0) {
            binding.tvTotalAmount.text = "Total: $intentTotal"
            binding.btnConfirm.setOnClickListener {
                confirmOrder(intentTotal)
            }
        } else {
            fetchCartTotalAndConfirm()
        }
    }

    // حساب المجموع من Firebase
    private fun fetchCartTotalAndConfirm() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)

        cartRef.get().addOnSuccessListener { snapshot ->
            var totalAmount = 0.0

            for (child in snapshot.children) {
                val item = child.getValue(CartItem::class.java)
                if (item != null) {
                    totalAmount += item.price * item.quantity
                }
            }

            binding.tvTotalAmount.text = "Total: $totalAmount"

            binding.btnConfirm.setOnClickListener {
                confirmOrder(totalAmount)
                cartRef.removeValue()
                startActivity(Intent(this, CustomerCategoryActivity::class.java))

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch cart data.", Toast.LENGTH_SHORT).show()
        }
    }

    //  تخزين الطلب في Firebase
    private fun confirmOrder(total: Double) {
        val userId = auth.currentUser?.uid ?: "guest"
        val orderRef = FirebaseDatabase.getInstance().getReference("orders").child(userId).push()

        val orderData = mapOf(
            "userId" to userId,
            "totalAmount" to total,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        orderRef.setValue(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "order placed successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "failed to place order", Toast.LENGTH_SHORT).show()
            }
    }
}
