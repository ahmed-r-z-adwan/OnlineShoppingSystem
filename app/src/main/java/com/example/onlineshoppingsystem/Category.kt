package com.example.onlineshoppingsystem

/**
 * Every field has a default so Kotlin also generates a no-argument constructor.
 * Firebase needs one: `getValue(Category::class.java)` instantiates the class reflectively
 * and throws `DatabaseException: does not define a no-argument constructor` without it.
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val imageBase64: String = "",
)
