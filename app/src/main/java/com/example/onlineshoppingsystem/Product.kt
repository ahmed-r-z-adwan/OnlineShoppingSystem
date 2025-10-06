package com.example.onlineshoppingsystem

import java.io.Serializable

data class Product(
    val id: String ,
    val categoryId: String ,
    val categoryName: String ,
    val name: String,
    val description: String,
    val imageBase64: String = "",   // الصورة كـ Base64 بدل رابط URL
    val price: Double,
    val rate: Float,
    val location: String
) : Serializable
