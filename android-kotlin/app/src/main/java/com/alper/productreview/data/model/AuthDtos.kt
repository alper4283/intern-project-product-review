package com.alper.productreview.data.model

data class RegisterRequestDto(val username: String, val email: String, val password: String)
data class LoginRequestDto(val username: String, val password: String)

data class AuthResponseDto(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
    val username: String,
    val role: String
)
