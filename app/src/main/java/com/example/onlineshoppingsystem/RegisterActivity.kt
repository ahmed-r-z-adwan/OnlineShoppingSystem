package com.example.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.jvm.java

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            register()
        }
        binding.tvClick.setOnClickListener {
            var intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }
    }

    private fun register() {
        // استخدمنا orEmpty عشان لما يكون قيمة الstring null ما يجي NullPointerException
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        // تحقق بسيط
        if (name.isEmpty()) {
            //  "Name is required"textهاد الكود لو تحقق بظهرلي رسالة تحت ال
            binding.etName.error = "Name is required"
            return      //هيك ما بنروح نسجل المستخدم أو نفحص باقي الحقول إلا لما يصلح الاسم
        } else binding.etName.error = null


        //Patterns.EMAIL_ADDRESS = كائن جاهز في أندرويد فيه Regular Expression للتحقق من شكل الإيميل
        //.matcher(email) → يطبق الفحص على النص اللي المستخدم كتبه.
        //.matches() → بترجع true إذا النص فعلاً شكله إيميل صحيح، false إذا مش صحيح.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Valid email required"
            return
        } else binding.etEmail.error = null

        if (password.length < 6) {
            binding.etPassword.error = "At least 6 characters"
            return
        } else binding.etPassword.error = null

        // إنشاء حساب في Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {              // معناها لما العملية تنجح نفذ الكود اللي جوه الأقواس
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                // حفظ بروفايل المستخدم في Realtime Database
                val user = User(uid = uid, name = name, email = email, role = if (email == "admin@shop.com") "admin" else "customer")
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created ", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "DB error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Auth error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
