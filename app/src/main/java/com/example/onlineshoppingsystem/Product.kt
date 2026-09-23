package com.example.onlineshoppingsystem

import java.io.Serializable

/** Defaults are required for Firebase deserialisation — see [Category]. */
data class Product(
    val id: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val name: String = "",
    val description: String = "",
    /** The image itself, Base64 encoded, rather than a URL to Cloud Storage. */
    val imageBase64: String = "",
    val price: Double = 0.0,
    val rate: Float = 0f,
    val location: String = "",
) : Serializable
