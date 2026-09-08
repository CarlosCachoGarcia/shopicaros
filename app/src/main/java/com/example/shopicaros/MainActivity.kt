package com.example.shopicaros

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.ui.products.ProductAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    private val productAdapter = ProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerProducts = findViewById(R.id.recyclerProducts)
        chipGroupCategories = findViewById(R.id.chipGroupCategories)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)

        recyclerProducts.layoutManager = LinearLayoutManager(this)
        recyclerProducts.adapter = productAdapter

        loadInitialData()
    }

    private fun loadInitialData() {

        lifecycleScope.launch {

            showLoading()

            try {

                val categories = RetrofitClient.api.getCategories()

                createCategoryChips(categories)

                val products = RetrofitClient.api.getProducts()

                productAdapter.updateProducts(products)

            } catch (e: Exception) {

                showError()

            } finally {

                hideLoading()
            }
        }
    }

    private fun createCategoryChips(categories: List<String>) {

        chipGroupCategories.removeAllViews()

        addCategoryChip(
            category = "Todos",
            selected = true
        )

        categories.forEach { category ->

            addCategoryChip(
                category = category,
                selected = false
            )
        }
    }

    private fun addCategoryChip(
        category: String,
        selected: Boolean
    ) {

        val chip = Chip(this)

        chip.text = category
        chip.isCheckable = true
        chip.isChecked = selected

        chip.setOnClickListener {

            filterByCategory(category)
        }

        chipGroupCategories.addView(chip)
    }

    private fun filterByCategory(category: String) {

        lifecycleScope.launch {

            /*
             * La US04 pide limpiar los datos anteriores
             * mientras llega la nueva petición.
             */
            productAdapter.clearProducts()

            showLoading()

            try {

                val products = if (category == "Todos") {

                    RetrofitClient.api.getProducts()

                } else {

                    RetrofitClient.api.getProductsByCategory(category)
                }

                productAdapter.updateProducts(products)

            } catch (e: Exception) {

                showError()

            } finally {

                hideLoading()
            }
        }
    }

    private fun showLoading() {

        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }

    private fun hideLoading() {

        progressBar.visibility = View.GONE
    }

    private fun showError() {

        tvError.visibility = View.VISIBLE
    }
}