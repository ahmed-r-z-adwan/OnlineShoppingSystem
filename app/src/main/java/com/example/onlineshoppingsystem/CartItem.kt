package com.example.onlineshoppingsystem

/** Defaults are required for Firebase deserialisation — see [Category]. */
data class CartItem(
    val productId: String = "",
    val userId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
)
