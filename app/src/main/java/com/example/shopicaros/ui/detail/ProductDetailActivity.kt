package com.example.shopicaros.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.shopicaros.R
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.data.repository.ProductRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserSessionRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var contentProduct: View
    private lateinit var progressDetail: ProgressBar

    private lateinit var ivProduct: ImageView
    private lateinit var tvProductTitle: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var chipCategory: Chip

    private lateinit var adminActionsContainer: LinearLayout

    private val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            RetrofitClient.api
        )
    }

    private val sessionRepository: UserSessionRepository by lazy {
        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(
            productRepository,
            sessionRepository
        )
    }

    private var errorDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_product_detail
        )

        bindViews()
        setupActions()
        observeUiState()
        observeEvents()

        val productId =
            intent.getIntExtra(
                EXTRA_PRODUCT_ID,
                INVALID_PRODUCT_ID
            )

        viewModel.loadProduct(productId)
    }
    private fun observeEvents() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.events.collect { event ->

                    when (event) {

                        is ProductDetailEvent.ProductDeleted -> {

                            setResult(
                                RESULT_OK,
                                createDeletedResult(
                                    event.productId
                                )
                            )

                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Producto eliminado",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }

                        is ProductDetailEvent.ShowMessage -> {

                            Toast.makeText(
                                this@ProductDetailActivity,
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun bindViews() {

        contentProduct =
            findViewById(R.id.contentProduct)

        progressDetail =
            findViewById(R.id.progressDetail)

        ivProduct =
            findViewById(R.id.ivProduct)

        tvProductTitle =
            findViewById(R.id.tvProductTitle)

        tvProductPrice =
            findViewById(R.id.tvProductPrice)

        tvProductDescription =
            findViewById(R.id.tvProductDescription)

        chipCategory =
            findViewById(R.id.chipCategory)

        adminActionsContainer =
            findViewById(R.id.adminActionsContainer)
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvBack
        ).setOnClickListener {

            finish()
        }
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

    private fun render(
        state: ProductDetailUiState
    ) {

        progressDetail.visibility =
            if (state.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        val product = state.product

        if (product != null) {

            contentProduct.visibility =
                View.VISIBLE

            renderProduct(product)

            renderAdminActions(
                canManageProduct =
                    state.canManageProduct
            )
        } else {

            contentProduct.visibility =
                View.GONE
        }

        if (
            state.errorMessage != null &&
            !errorDialogShown
        ) {

            errorDialogShown = true

            showProductUnavailable(
                state.errorMessage
            )
        }
    }

    private fun renderProduct(
        product: Product
    ) {

        tvProductTitle.text =
            product.title

        tvProductPrice.text =
            "$${String.format("%.2f", product.price)}"

        tvProductDescription.text =
            product.description

        chipCategory.text =
            product.category

        Glide
            .with(this)
            .load(product.image)
            .into(ivProduct)
    }

    private fun renderAdminActions(
        canManageProduct: Boolean
    ) {

        adminActionsContainer.removeAllViews()

        if (!canManageProduct) {
            return
        }

        val editButton =
            MaterialButton(this).apply {

                text = "Editar"

                setOnClickListener {
                    onEditRequested()
                }
            }

        val deleteButton =
            MaterialButton(this).apply {

                text = "Eliminar"

                setOnClickListener {
                    onDeleteRequested()
                }
            }

        adminActionsContainer.addView(
            editButton
        )

        adminActionsContainer.addView(
            deleteButton
        )
    }

    private fun onEditRequested() {

        val product =
            viewModel.uiState.value.product
                ?: return

        editProductLauncher.launch(
            EditProductActivity.createIntent(
                this,
                product.id
            )
        )
    }

    private fun onDeleteRequested() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar producto")
            .setMessage(
                "¿Estás seguro de que deseas eliminar este producto?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton(
                "Eliminar"
            ) { _, _ ->

                viewModel.deleteProduct()
            }
            .show()
    }

    private fun showProductUnavailable(
        message: String
    ) {

        MaterialAlertDialogBuilder(this)
            .setTitle(
                "Producto no disponible"
            )
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Regresar"
            ) { _, _ ->

                finish()
            }
            .show()
    }

    companion object {

        private const val EXTRA_PRODUCT_ID =
            "extra_product_id"

        private const val INVALID_PRODUCT_ID =
            -1

        fun createIntent(
            context: Context,
            productId: Int
        ): Intent {

            return Intent(
                context,
                ProductDetailActivity::class.java
            ).apply {

                putExtra(
                    EXTRA_PRODUCT_ID,
                    productId
                )
            }
        }

        private const val RESULT_ACTION =
            "product_result_action"

        private const val ACTION_UPDATED =
            "updated"

        private const val ACTION_DELETED =
            "deleted"

        private const val RESULT_PRODUCT_ID =
            "result_product_id"

        private const val RESULT_TITLE =
            "result_title"

        private const val RESULT_PRICE =
            "result_price"

        private const val RESULT_DESCRIPTION =
            "result_description"

        private const val RESULT_CATEGORY =
            "result_category"

        private const val RESULT_IMAGE =
            "result_image"


        private fun createDeletedResult(
            productId: Int
        ): Intent {

            return Intent().apply {

                putExtra(
                    RESULT_ACTION,
                    ACTION_DELETED
                )

                putExtra(
                    RESULT_PRODUCT_ID,
                    productId
                )
            }
        }

        private fun createUpdatedResult(
            product: Product
        ): Intent {

            return Intent().apply {

                putExtra(
                    RESULT_ACTION,
                    ACTION_UPDATED
                )

                putExtra(
                    RESULT_PRODUCT_ID,
                    product.id
                )

                putExtra(
                    RESULT_TITLE,
                    product.title
                )

                putExtra(
                    RESULT_PRICE,
                    product.price
                )

                putExtra(
                    RESULT_DESCRIPTION,
                    product.description
                )

                putExtra(
                    RESULT_CATEGORY,
                    product.category
                )

                putExtra(
                    RESULT_IMAGE,
                    product.image
                )
            }
        }
    }
    private val editProductLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val product =
                    EditProductActivity
                        .getUpdatedProduct(
                            result.data
                        )

                if (product != null) {

                    viewModel.applyUpdatedProduct(
                        product
                    )

                    Toast.makeText(
                        this,
                        "Producto actualizado (Simulación)",
                        Toast.LENGTH_SHORT
                    ).show()

                    setResult(
                        RESULT_OK,
                        createUpdatedResult(
                            product
                        )
                    )
                }
            }
        }
}