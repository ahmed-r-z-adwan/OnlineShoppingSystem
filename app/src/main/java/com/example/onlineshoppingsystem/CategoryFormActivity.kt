package com.example.onlineshoppingsystem

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityCategoryFormBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream
import java.util.*

class CategoryFormActivity : AppCompatActivity() {

    private lateinit var b: ActivityCategoryFormBinding
    private val ref = FirebaseDatabase.getInstance().getReference("categories")

    private var catId: String? = null  // null = إدخال جديد
    private var imageBase64: String = "" // الصورة المحوّلة
    private var imageUri: Uri? = null

    // لاختيار صورة من المعرض
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            b.imgPreview.setImageURI(uri) // عرض مؤقت

            // حوّل الصورة إلى Base64
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCategoryFormBinding.inflate(layoutInflater)
        setContentView(b.root)

        // لو في بيانات جاية من CategoryListActivity → تعديل
        catId = intent.getStringExtra("catId")
        val catName = intent.getStringExtra("catName")
        val catImage = intent.getStringExtra("catImage")

        if (catId != null) {
            b.etName.setText(catName)
            b.btnSave.text = "Update Category"

            // لو عنده صورة محفوظة Base64 → فكها
            if (!catImage.isNullOrEmpty()) {
                try {
                    val bytes = Base64.decode(catImage, Base64.DEFAULT)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    b.imgPreview.setImageBitmap(bitmap)
                    imageBase64 = catImage
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // زر اختيار صورة
        b.btnPickImage.setOnClickListener { pickImage.launch("image/*") }

        // زر حفظ
        b.btnSave.setOnClickListener { saveCategory() }
    }

    private fun saveCategory() {
        val name = b.etName.text.toString().trim()
        if (name.isEmpty()) {
            b.etName.error = "Enter category name"
            return
        }

        if (catId == null) {
            // إضافة جديدة
            val id = UUID.randomUUID().toString()
            val cat = Category(id, name, imageBase64)
            ref.child(id).setValue(cat)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            // تحديث
            val cat = Category(catId!!, name, imageBase64)
            ref.child(catId!!).setValue(cat)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}
