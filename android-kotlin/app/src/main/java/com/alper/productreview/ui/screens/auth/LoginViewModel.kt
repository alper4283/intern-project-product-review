package com.alper.productreview.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alper.productreview.data.auth.TokenStore
import com.alper.productreview.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isSignUp: Boolean = false,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)


class LoginViewModel(
    private val repo: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun onUsernameChange(v: String) = _state.value.let { _state.value = it.copy(username = v) }
    fun onPasswordChange(v: String) = _state.value.let { _state.value = it.copy(password = v) }

    fun toggleMode() {
        val s = _state.value
        _state.value = s.copy(isSignUp = !s.isSignUp, error = null)
    }

    fun onEmailChange(v: String) = _state.value.let { _state.value = it.copy(email = v) }


    fun submitAuth() {
        val s = _state.value

        if (s.username.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Username and password are required.")
            return
        }
        if (s.isSignUp && s.email.isBlank()) {
            _state.value = s.copy(error = "Email is required for sign up.")
            return
        }

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val resp = if (s.isSignUp) {
                    repo.register(
                        username = s.username.trim(),
                        email = s.email.trim(),
                        password = s.password
                    )
                } else {
                    repo.login(s.username.trim(), s.password)
                }

                tokenStore.saveToken(resp.token)
                _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Authentication failed"
                )
            }
        }
    }

}
