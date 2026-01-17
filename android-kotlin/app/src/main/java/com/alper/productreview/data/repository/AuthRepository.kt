package com.alper.productreview.data.repository

import com.alper.productreview.data.api.ApiService
import com.alper.productreview.data.model.AuthResponseDto
import com.alper.productreview.data.model.LoginRequestDto
import com.alper.productreview.data.model.RegisterRequestDto

class AuthRepository(private val api: ApiService) {
    suspend fun login(username: String, password: String): AuthResponseDto {
        return api.login(LoginRequestDto(username = username, password = password))
    }

    suspend fun register(username: String, email: String, password: String): AuthResponseDto {
        return api.register(
            RegisterRequestDto(username = username, email = email, password = password)
        )
    }
}
