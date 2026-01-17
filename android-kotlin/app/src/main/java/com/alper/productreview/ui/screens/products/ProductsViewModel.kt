package com.alper.productreview.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alper.productreview.data.model.ProductDto
import com.alper.productreview.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductsUiState(
    val items: List<ProductDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val isLast: Boolean = false,
    val sort: String? = "price,asc", // default sort
    val search: String = "",
    val selectedCategory: String? = null
)

class ProductsViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsUiState())
    val state: StateFlow<ProductsUiState> = _state

    private val pageSize = 20

    fun loadFirstPage() {
        _state.value = _state.value.copy(items = emptyList(), page = 0, isLast = false)
        loadNextPage(reset = true)
    }

    fun loadNextPage(reset: Boolean = false) {
        val s = _state.value
        if (s.isLoading || s.isLast) return

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val nextPage = if (reset) 0 else s.page
                val resp = repo.getProducts(page = nextPage, size = pageSize, sort = s.sort)

                val base = if (reset) emptyList() else s.items

                // Merge + dedupe by stable key (id) to avoid Compose duplicate-key crashes
                val merged = (base + resp.content).distinctBy { it.id }

                // If this page adds nothing new (overlap due to unstable sort),
                // don't keep advancing forever.
                val addedSomething = merged.size > base.size

                _state.value = _state.value.copy(
                    items = merged,
                    isLoading = false,
                    page = if (addedSomething) nextPage + 1 else nextPage,
                    isLast = resp.last || !addedSomething
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load products"
                )
            }
        }
    }


    fun setSort(newSort: String?) {
        _state.value = _state.value.copy(sort = newSort)
        loadFirstPage()
    }

    fun setSearch(q: String) {
        _state.value = _state.value.copy(search = q)
        // client-side filter -> no refetch necessary
    }

    fun setCategory(cat: String?) {
        _state.value = _state.value.copy(selectedCategory = cat)
        // client-side filter -> no refetch necessary
    }
}
