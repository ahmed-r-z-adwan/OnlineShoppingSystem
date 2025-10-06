package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // زر تسجيل الدخول
        binding.btnLogin.setOnClickListener {
            login()
        }

        // زر إنشاء حساب جديد → يودّي لواجهة التسجيل
        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        // تحقق من صحة البيانات
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Valid email required"
            return
        } else binding.etEmail.error = null

        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            return
        } else binding.etPassword.error = null

        // تسجيل الدخول باستخدام Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                // جلب الدور (role) من Realtime Database
                val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
                ref.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val role = snapshot.child("role").value.toString()
                        Toast.makeText(this, "Login successful: $role", Toast.LENGTH_SHORT).show()

                        if (role == "admin") {
                            startActivity(Intent(this, CategoryListActivity::class.java))
                        } else {
                            startActivity(Intent(this, CustomerCategoryActivity::class.java))
                        }
                        finish()
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
