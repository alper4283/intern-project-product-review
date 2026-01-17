package com.alper.productreview.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alper.productreview.data.model.ProductDto
import com.alper.productreview.data.model.ReviewDto
import com.alper.productreview.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val product: ProductDto? = null,
    val reviews: List<ReviewDto> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null
)

class ProductDetailViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state

    fun load(productId: Long) {
        _state.value = ProductDetailUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val product = repo.getProduct(productId)
                val reviews = repo.getReviews(productId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    product = product,
                    reviews = reviews,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load product"
                )
            }
        }
    }

    fun addReview(productId: Long, rating: Int, comment: String?) {
        if (rating !in 1..5) {
            _state.value = _state.value.copy(submitError = "Rating must be 1 to 5.")
            return
        }

        _state.value = _state.value.copy(isSubmitting = true, submitError = null)

        viewModelScope.launch {
            try {
                val created = repo.addReview(productId, rating, comment?.takeIf { it.isNotBlank() })
                // Optimistic: prepend new review
                val updated = listOf(created) + _state.value.reviews
                _state.value = _state.value.copy(isSubmitting = false, reviews = updated)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    submitError = e.message ?: "Failed to submit review"
                )
            }
        }
    }
}
