package com.mzansi.pricecompare.api.models

data class User(
    val id: Int,
    val name: String,
    val email: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class AuthError(
    val error: String
)

data class UserResponse(
    val user: User
)
