package com.example.fitsync.user

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = "",
    val height: Int = 0,
    val weight: Int = 0,
    val goal: Int = 0,
)
