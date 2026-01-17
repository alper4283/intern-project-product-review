package com.alper.productreview.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alper.productreview.ui.util.formatIsoTimestamp
import com.alper.productreview.ui.util.formatRating
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    stateFlow: StateFlow<ProductDetailUiState>,
    onLoad: () -> Unit,
    onBack: () -> Unit,
    onSubmitReview: (rating: Int, comment: String?) -> Unit
) {
    val state by stateFlow.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onLoad() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.product?.name ?: "Product") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                state.error != null -> {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onLoad) { Text("Retry") }
                    }
                }
                else -> {
                    val p = state.product
                    if (p == null) {
                        Text("No product data", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(p.name, style = MaterialTheme.typography.headlineSmall)
                                Spacer(Modifier.height(4.dp))
                                Text(p.category, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                                Text("₺${p.price}", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(6.dp))
                                Text("⭐ ${formatRating(p.averageRating)} • ${p.reviewCount} reviews")
                                if (!p.description.isNullOrBlank()) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(p.description!!, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            item {
                                Divider()
                                Text("Reviews", style = MaterialTheme.typography.titleMedium)
                                if (state.submitError != null) {
                                    Text(state.submitError!!, color = MaterialTheme.colorScheme.error)
                                }
                            }

                            items(state.reviews, key = { it.id }) { r ->
                                Card {
                                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("⭐ ${r.rating}  •  ${r.username}", style = MaterialTheme.typography.bodyMedium)
                                        if (!r.comment.isNullOrBlank()) Text(r.comment!!)
                                        Text(formatIsoTimestamp(r.createdAt), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            if (state.reviews.isEmpty()) {
                                item { Text("No reviews yet.") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddReviewDialog(
            isSubmitting = state.isSubmitting,
            onDismiss = { if (!state.isSubmitting) showDialog = false },
            onSubmit = { rating, comment ->
                onSubmitReview(rating, comment)
                // close dialog after submit kicks off (optional)
                showDialog = false
            }
        )
    }
}

@Composable
private fun AddReviewDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, String?) -> Unit
) {
    var ratingText by remember { mutableStateOf("5") }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ratingText,
                    onValueChange = { ratingText = it },
                    label = { Text("Rating (1-5)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isSubmitting
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (optional)") },
                    enabled = !isSubmitting
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rating = ratingText.toIntOrNull() ?: 0
                    onSubmit(rating, comment)
                },
                enabled = !isSubmitting
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancel")
            }
        }
    )
}
