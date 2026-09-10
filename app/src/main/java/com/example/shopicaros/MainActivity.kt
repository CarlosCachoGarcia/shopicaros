package com.example.shopicaros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.data.repository.ProductRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.cart.CartActivity
import com.example.shopicaros.ui.detail.ProductDetailActivity
import com.example.shopicaros.ui.login.LoginActivity
import com.example.shopicaros.ui.products.AddProductActivity
import com.example.shopicaros.ui.products.ProductAdapter
import com.example.shopicaros.ui.products.ProductUiState
import com.example.shopicaros.ui.products.ProductViewModel
import com.example.shopicaros.ui.products.ProductViewModelFactory
import com.example.shopicaros.ui.session.SessionEvent
import com.example.shopicaros.ui.session.SessionViewModel
import com.example.shopicaros.ui.session.SessionViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var errorContainer: LinearLayout
    private lateinit var btnRetry: MaterialButton

    private lateinit var bottomNavigation: BottomNavigationView

    private val sessionRepository: UserSessionRepository by lazy {

        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val sessionViewModel: SessionViewModel by viewModels {

        SessionViewModelFactory(
            sessionRepository
        )
    }

    private val productAdapter by lazy {

        ProductAdapter { productId ->

            startActivity(
                ProductDetailActivity.createIntent(
                    this,
                    productId
                )
            )
        }
    }

    private val repository: ProductRepository by lazy {

        ProductRepositoryImpl(
            RetrofitClient.api
        )
    }

    private val viewModel: ProductViewModel by viewModels {

        ProductViewModelFactory(
            repository
        )
    }

    private var renderedCategories: List<String> =
        emptyList()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        if (!sessionRepository.isLoggedIn()) {

            goToLogin()
            return
        }

        setContentView(
            R.layout.activity_main
        )

        bindViews()

        setupRecyclerView()

        configureBottomNavigation()

        setupActions()

        observeUiState()

        observeSessionEvents()
    }

    private fun bindViews() {

        recyclerProducts =
            findViewById(
                R.id.recyclerProducts
            )

        chipGroupCategories =
            findViewById(
                R.id.chipGroupCategories
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )

        tvError =
            findViewById(
                R.id.tvError
            )

        errorContainer =
            findViewById(
                R.id.errorContainer
            )

        btnRetry =
            findViewById(
                R.id.btnRetry
            )

        bottomNavigation =
            findViewById(
                R.id.bottomNavigation
            )
    }

    private fun configureBottomNavigation() {

        val role =
            sessionRepository.getRole()

        val addProductItem =
            bottomNavigation.menu.findItem(
                R.id.navAddProduct
            )

        val cartItem =
            bottomNavigation.menu.findItem(
                R.id.navCart
            )

        // Solo Administrador puede agregar productos.
        addProductItem.isVisible =
            role ==
                    UserRole.ADMINISTRADOR

        // Solo Cliente puede acceder al carrito.
        cartItem.isVisible =
            role ==
                    UserRole.CLIENTE

        bottomNavigation
            .setOnItemSelectedListener { item ->

                when (item.itemId) {

                    R.id.navAddProduct -> {

                        if (
                            sessionRepository.getRole() ==
                            UserRole.ADMINISTRADOR
                        ) {

                            startActivity(
                                Intent(
                                    this,
                                    AddProductActivity::class.java
                                )
                            )
                        }

                        false
                    }

                    R.id.navCart -> {

                        if (
                            sessionRepository.getRole() ==
                            UserRole.CLIENTE
                        ) {

                            startActivity(
                                Intent(
                                    this,
                                    CartActivity::class.java
                                )
                            )
                        }

                        false
                    }

                    R.id.navLogout -> {

                        sessionViewModel.logout()

                        false
                    }

                    else -> false
                }
            }
    }

    private fun setupActions() {

        btnRetry.setOnClickListener {

            viewModel.retry()
        }
    }

    private fun observeSessionEvents() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                sessionViewModel.events
                    .collect { event ->

                        when (event) {

                            SessionEvent.LoggedOut -> {

                                goToLogin()
                            }
                        }
                    }
            }
        }
    }

    private fun goToLogin() {

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(
            intent
        )

        finish()
    }

    private fun setupRecyclerView() {

        recyclerProducts.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerProducts.adapter =
            productAdapter
    }

    private fun observeUiState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState
                    .collect { state ->

                        render(
                            state
                        )
                    }
            }
        }
    }

    private fun render(
        state: ProductUiState
    ) {

        progressBar.visibility =
            if (state.isLoading) {

                View.VISIBLE

            } else {

                View.GONE
            }

        val hasError =
            state.errorMessage != null

        errorContainer.visibility =
            if (
                hasError &&
                !state.isLoading
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }

        recyclerProducts.visibility =
            if (
                !state.isLoading &&
                !hasError
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }

        tvError.text =
            state.errorMessage
                ?: "No se pudieron cargar los productos."

        productAdapter.submitList(
            state.products
        )

        if (
            renderedCategories !=
            state.categories
        ) {

            renderedCategories =
                state.categories

            createCategoryChips(
                state.categories
            )
        }

        updateSelectedCategory(
            state.selectedCategory
        )
    }

    private fun createCategoryChips(
        categories: List<String>
    ) {

        chipGroupCategories.removeAllViews()

        val allCategories =
            listOf(
                ProductUiState.ALL_CATEGORY
            ) + categories

        allCategories.forEach { category ->

            val chip =
                Chip(this).apply {

                    text =
                        category

                    isCheckable =
                        true

                    setOnClickListener {

                        viewModel.selectCategory(
                            category
                        )
                    }
                }

            chipGroupCategories.addView(
                chip
            )
        }
    }

    private fun updateSelectedCategory(
        selectedCategory: String
    ) {

        for (
        index in 0
                until chipGroupCategories.childCount
        ) {

            val chip =
                chipGroupCategories
                    .getChildAt(index)
                        as? Chip
                    ?: continue

            chip.isChecked =
                chip.text.toString() ==
                        selectedCategory
        }
    }
}