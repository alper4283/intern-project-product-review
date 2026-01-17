package com.alper.productreview.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alper.productreview.data.model.ProductDto
import com.alper.productreview.ui.util.formatRating
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProductsScreen(
    stateFlow: StateFlow<ProductsUiState>,
    onLoadNext: () -> Unit,
    onReload: () -> Unit,
    onSortChange: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onOpenProduct: (Long) -> Unit
) {
    val state by stateFlow.collectAsState()

    // initial load
    LaunchedEffect(Unit) { onReload() }

    val categories = remember(state.items) {
        state.items.map { it.category }.distinct().sorted()
    }

    val filtered = remember(state.items, state.search, state.selectedCategory) {
        state.items.filter { p ->
            val catOk = state.selectedCategory == null || p.category == state.selectedCategory
            val searchOk = state.search.isBlank() || p.name.contains(state.search, ignoreCase = true)
            catOk && searchOk
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.search,
                onValueChange = onSearchChange,
                label = { Text("Search") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            SortMenu(current = state.sort, onSortChange = onSortChange)
        }

        Spacer(Modifier.height(8.dp))

        CategoryMenu(
            categories = categories,
            selected = state.selectedCategory,
            onSelect = onCategoryChange
        )

        Spacer(Modifier.height(8.dp))

        if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onReload) { Text("Retry") }
            return@Column
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { p ->
                ProductCard(p, onClick = { onOpenProduct(p.id) })
            }

            item {
                Spacer(Modifier.height(8.dp))
                if (!state.isLast) {
                    Button(
                        onClick = onLoadNext,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                        }
                        Text("Load more")
                    }
                } else {
                    Text("End of list", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ProductCard(p: ProductDto, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(p.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
            Text(p.category, style = MaterialTheme.typography.bodySmall)
            Text("₺${p.price}", style = MaterialTheme.typography.bodyMedium)
            Text("⭐ ${formatRating(p.averageRating)}  •  ${p.reviewCount} reviews", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SortMenu(current: String?, onSortChange: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                when (current) {
                    "reviewCount,desc" -> "Most reviewed"
                    "price,asc" -> "Price ↑"
                    "price,desc" -> "Price ↓"
                    "name,asc" -> "Name A-Z"
                    "averageRating,desc" -> "Rating ↓"
                    else -> "Sort"
                }
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Most reviewed") },
                onClick = { expanded = false; onSortChange("reviewCount,desc") }
            )
            DropdownMenuItem(text = { Text("Price ↑") }, onClick = { expanded = false; onSortChange("price,asc") })
            DropdownMenuItem(text = { Text("Price ↓") }, onClick = { expanded = false; onSortChange("price,desc") })
            DropdownMenuItem(text = { Text("Name A-Z") }, onClick = { expanded = false; onSortChange("name,asc") })
            DropdownMenuItem(text = { Text("Rating ↓") }, onClick = { expanded = false; onSortChange("averageRating,desc") })
        }
    }
}

@Composable
private fun CategoryMenu(categories: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected ?: "Category")
        }
        if (selected != null) {
            TextButton(onClick = { onSelect(null) }) { Text("Clear") }
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        categories.forEach { cat ->
            DropdownMenuItem(
                text = { Text(cat) },
                onClick = { expanded = false; onSelect(cat) }
            )
        }
    }
}
