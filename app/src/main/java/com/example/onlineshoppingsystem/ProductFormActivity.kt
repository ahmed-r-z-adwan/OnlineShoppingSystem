package com.example.onlineshoppingsystem

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshoppingsystem.databinding.ActivityProductFormBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream
import java.util.*

class ProductFormActivity : AppCompatActivity() {

    private lateinit var b: ActivityProductFormBinding
    private var imageUri: Uri? = null
    private var editingProduct: Product? = null

    private var categoryId: String? = null   //  ID التصنيف
    private val categories = mutableListOf<String>()  //  من firebase

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            b.imgPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(b.root)

        categoryId = intent.getStringExtra("categoryId")

        //  جلب التصنيفات من Firebase بدل القائمة الثابتة
        val catRef = FirebaseDatabase.getInstance().getReference("categories")
        catRef.get().addOnSuccessListener { snapshot ->
            categories.clear()
            for (ch in snapshot.children) {
                val cat = ch.getValue(Category::class.java)
                if (cat != null) categories.add(cat.name)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            b.spCategory.adapter = adapter

            // لو تعديل، نضبط الـ spinner بعد ما يتعبى
            editingProduct?.let {
                val pos = categories.indexOf(it.categoryName)
                if (pos >= 0) b.spCategory.setSelection(pos)
            }
        }

        // لو جاي تعديل
        editingProduct = intent.getSerializableExtra("product") as? Product
        if (editingProduct != null) {
            fillForm(editingProduct!!)
            b.btnSave.text = "Update Product"
        }

        b.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        b.btnSave.setOnClickListener { saveProduct() }
    }

    private fun fillForm(product: Product) {
        b.etName.setText(product.name)
        b.etDesc.setText(product.description)
        b.etPrice.setText(product.price.toString())
        b.etLocation.setText(product.location)
        b.etRate.setText(product.rate.toString())

        if (product.imageBase64.isNotEmpty()) {
            val bytes = Base64.decode(product.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            b.imgPreview.setImageBitmap(bitmap)
        }
    }

    private fun saveProduct() {
        val name = b.etName.text.toString().trim()
        val desc = b.etDesc.text.toString().trim()
        val price = b.etPrice.text.toString().toDoubleOrNull()
        val location = b.etLocation.text.toString().trim()
        val rate = b.etRate.text.toString().toFloatOrNull()
        val category = b.spCategory.selectedItem?.toString() ?: ""

        if (name.isEmpty() || desc.isEmpty() || price == null || rate == null || category.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        var base64Image = ""
        imageUri?.let {
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
        }

        b.progress.visibility = View.VISIBLE
        val dbRef = FirebaseDatabase.getInstance().getReference("products")

        if (editingProduct == null) {
            val id = UUID.randomUUID().toString()
            val product = Product(
                id = id,
                categoryId = categoryId ?: "",
                categoryName = category,
                name = name,
                description = desc,
                price = price,
                imageBase64 = base64Image,
                location = location,
                rate = rate
            )
            dbRef.child(id).setValue(product).addOnSuccessListener {
                b.progress.visibility = View.GONE
                Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            val updated = editingProduct!!.copy(
                categoryId = categoryId ?: editingProduct!!.categoryId,
                categoryName = category,
                name = name,
                description = desc,
                price = price,
                location = location,
                rate = rate,
                imageBase64 = if (base64Image.isNotEmpty()) base64Image else editingProduct!!.imageBase64
            )
            dbRef.child(editingProduct!!.id).setValue(updated).addOnSuccessListener {
                b.progress.visibility = View.GONE
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
