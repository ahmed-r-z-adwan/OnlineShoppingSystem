package com.example.onlineshoppingsystem

/** Defaults are required for Firebase deserialisation — see [Category]. */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "customer",
)
