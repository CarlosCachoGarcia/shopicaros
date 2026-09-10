package com.example.shopicaros

import android.os.Bundle
import android.view.View
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
import com.example.shopicaros.ui.products.ProductAdapter
import com.example.shopicaros.ui.products.ProductUiState
import com.example.shopicaros.ui.products.ProductViewModel
import com.example.shopicaros.ui.products.ProductViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import com.example.shopicaros.ui.detail.ProductDetailActivity
import android.content.Intent
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.example.shopicaros.ui.session.SessionEvent
import com.example.shopicaros.ui.session.SessionViewModel
import com.example.shopicaros.ui.session.SessionViewModelFactory
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var btnLogout: MaterialButton




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
        ProductViewModelFactory(repository)
    }

    private var renderedCategories: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        bindViews()
        setupRecyclerView()
        setupSessionActions()
        observeUiState()
        observeSessionEvents()
    }

    private fun bindViews() {

        recyclerProducts =
            findViewById(R.id.recyclerProducts)

        chipGroupCategories =
            findViewById(R.id.chipGroupCategories)

        progressBar =
            findViewById(R.id.progressBar)

        tvError =
            findViewById(R.id.tvError)
        btnLogout =
            findViewById(R.id.btnLogout)


    }
    private fun setupSessionActions() {

        btnLogout.setOnClickListener {

            sessionViewModel.logout()
        }
    }

    private fun observeSessionEvents() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                sessionViewModel.events.collect { event ->

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

        startActivity(intent)
        finish()
    }
    private fun setupRecyclerView() {

        recyclerProducts.layoutManager =
            LinearLayoutManager(this)

        recyclerProducts.adapter =
            productAdapter
    }

    private fun observeUiState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    render(state)
                }
            }
        }
    }

    private fun render(state: ProductUiState) {

        progressBar.visibility =
            if (state.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvError.visibility =
            if (state.errorMessage != null) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvError.text =
            state.errorMessage ?: ""

        productAdapter.submitList(
            state.products
        )

        if (renderedCategories != state.categories) {

            renderedCategories = state.categories

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
            listOf(ProductUiState.ALL_CATEGORY) +
                    categories

        allCategories.forEach { category ->

            val chip = Chip(this).apply {

                text = category

                isCheckable = true

                setOnClickListener {

                    viewModel.selectCategory(
                        category
                    )
                }
            }

            chipGroupCategories.addView(chip)
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
                chipGroupCategories.getChildAt(index)
                        as? Chip
                    ?: continue

            chip.isChecked =
                chip.text.toString() ==
                        selectedCategory
        }
    }
}