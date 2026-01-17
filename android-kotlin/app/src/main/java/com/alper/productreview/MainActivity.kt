package com.alper.productreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.alper.productreview.data.api.ApiClient
import com.alper.productreview.data.auth.TokenStore
import com.alper.productreview.data.repository.AuthRepository
import com.alper.productreview.data.repository.ProductRepository
import com.alper.productreview.ui.screens.auth.LoginScreen
import com.alper.productreview.ui.screens.auth.LoginViewModel
import com.alper.productreview.ui.screens.detail.ProductDetailScreen
import com.alper.productreview.ui.screens.detail.ProductDetailViewModel
import com.alper.productreview.ui.screens.products.ProductsScreen
import com.alper.productreview.ui.screens.products.ProductsViewModel
import com.alper.productreview.ui.theme.ProductReviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Build dependencies once here (demo-simple)
        val api = ApiClient.create(applicationContext)
        val tokenStore = TokenStore(applicationContext)
        val authRepo = AuthRepository(api)
        val productRepo = ProductRepository(api)
        val detailVm = ProductDetailViewModel(productRepo)
        val productsVm = ProductsViewModel(productRepo)
        val loginVm = LoginViewModel(authRepo, tokenStore)

        setContent {
            ProductReviewTheme {
                AppNav(loginVm = loginVm, productsVm = productsVm, detailVm = detailVm)
            }
        }

    }
}

@Composable
private fun AppNav(
    loginVm: LoginViewModel,
    productsVm: ProductsViewModel,
    detailVm: ProductDetailViewModel
) {

    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    stateFlow = loginVm.state,
                    onUsernameChange = loginVm::onUsernameChange,
                    onEmailChange = loginVm::onEmailChange,
                    onPasswordChange = loginVm::onPasswordChange,
                    onSubmit = loginVm::submitAuth,
                    onToggleMode = loginVm::toggleMode,
                    onLoggedInNavigateNext = {
                        navController.navigate("products") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }


            composable("products") {
                ProductsScreen(
                    stateFlow = productsVm.state,
                    onLoadNext = { productsVm.loadNextPage() },
                    onReload = { productsVm.loadFirstPage() },
                    onSortChange = { productsVm.setSort(it) },
                    onSearchChange = { productsVm.setSearch(it) },
                    onCategoryChange = { productsVm.setCategory(it) },
                    onOpenProduct = { productId ->
                        navController.navigate("product/$productId")
                    }
                )
            }

            composable("product/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
                if (id == null) {
                    Text("Invalid product id")
                    return@composable
                }

                ProductDetailScreen(
                    stateFlow = detailVm.state,
                    onLoad = { detailVm.load(id) },
                    onBack = { navController.popBackStack() },
                    onSubmitReview = { rating, comment -> detailVm.addReview(id, rating, comment) }
                )
            }

        }
    }
}
