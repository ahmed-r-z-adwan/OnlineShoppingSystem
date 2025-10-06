package com.example.onlineshoppingsystem

data class User(
    val uid: String,
    val name: String ,
    val email: String,
    val role: String = "customer"
)
