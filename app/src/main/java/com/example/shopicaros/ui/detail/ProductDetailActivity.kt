package com.example.shopicaros.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.shopicaros.R
import com.example.shopicaros.data.local.SharedPreferencesCartLocalDataSource
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.CartRepository
import com.example.shopicaros.data.repository.CartRepositoryImpl
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.data.repository.ProductRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.cart.CartEvent
import com.example.shopicaros.ui.cart.CartViewModel
import com.example.shopicaros.ui.cart.CartViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var contentProduct: View
    private lateinit var progressDetail: ProgressBar

    private lateinit var ivProduct: ImageView
    private lateinit var tvProductTitle: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var chipCategory: Chip

    // US09 - Carrito
    private lateinit var cartActionsContainer: LinearLayout
    private lateinit var btnDecreaseQuantity: MaterialButton
    private lateinit var btnIncreaseQuantity: MaterialButton
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var tvQuantity: TextView

    // Acciones de administrador
    private lateinit var adminActionsContainer: LinearLayout

    private var quantity = 1

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

    private val cartLocalDataSource by lazy {
        SharedPreferencesCartLocalDataSource(
            applicationContext
        )
    }

    private val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(
            RetrofitClient.api,
            cartLocalDataSource
        )
    }

    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(
            productRepository,
            sessionRepository
        )
    }

    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            cartRepository,
            sessionRepository
        )
    }

    private var errorDialogShown = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_product_detail
        )

        bindViews()
        setupActions()

        observeUiState()
        observeEvents()

        observeCartState()
        observeCartEvents()

        val productId =
            intent.getIntExtra(
                EXTRA_PRODUCT_ID,
                INVALID_PRODUCT_ID
            )

        viewModel.loadProduct(
            productId
        )
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

        // US09
        cartActionsContainer =
            findViewById(R.id.cartActionsContainer)

        btnDecreaseQuantity =
            findViewById(R.id.btnDecreaseQuantity)

        btnIncreaseQuantity =
            findViewById(R.id.btnIncreaseQuantity)

        btnAddToCart =
            findViewById(R.id.btnAddToCart)

        tvQuantity =
            findViewById(R.id.tvQuantity)

        adminActionsContainer =
            findViewById(R.id.adminActionsContainer)
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvBack
        ).setOnClickListener {

            finish()
        }

        // US09 - Restar cantidad
        btnDecreaseQuantity.setOnClickListener {

            if (quantity > 1) {

                quantity--

                updateQuantityText()
            }
        }

        // US09 - Aumentar cantidad
        btnIncreaseQuantity.setOnClickListener {

            quantity++

            updateQuantityText()
        }

        // US09 - Agregar producto
        btnAddToCart.setOnClickListener {

            val product =
                viewModel.uiState.value.product
                    ?: return@setOnClickListener

            cartViewModel.addProduct(
                product = product,
                quantity = quantity
            )
        }
    }

    private fun updateQuantityText() {

        tvQuantity.text =
            quantity.toString()
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
                                "Producto eliminado (Simulación)",
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

    private fun observeCartState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                cartViewModel.uiState.collect { state ->

                    btnAddToCart.isEnabled =
                        !state.isAdding

                    btnDecreaseQuantity.isEnabled =
                        !state.isAdding

                    btnIncreaseQuantity.isEnabled =
                        !state.isAdding

                    btnAddToCart.text =
                        if (state.isAdding) {
                            "Agregando..."
                        } else {
                            "Agregar al carrito"
                        }
                }
            }
        }
    }

    private fun observeCartEvents() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                cartViewModel.events.collect { event ->

                    when (event) {

                        is CartEvent.ProductAdded -> {

                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Producto agregado al carrito. Cantidad total: ${event.quantity}",
                                Toast.LENGTH_SHORT
                            ).show()

                            quantity = 1

                            updateQuantityText()
                        }

                        is CartEvent.ShowMessage -> {

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

    private fun render(
        state: ProductDetailUiState
    ) {

        progressDetail.visibility =
            if (
                state.isLoading ||
                state.isDeleting
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        val product =
            state.product

        if (product != null) {

            contentProduct.visibility =
                View.VISIBLE

            renderProduct(
                product
            )

            // US09
            renderCartActions(
                canAddToCart =
                    state.canAddToCart
            )

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

    // US09
    private fun renderCartActions(
        canAddToCart: Boolean
    ) {

        cartActionsContainer.visibility =
            if (canAddToCart) {
                View.VISIBLE
            } else {
                View.GONE
            }
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
            .setTitle(
                "Eliminar producto"
            )
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
            .setMessage(
                message
            )
            .setCancelable(
                false
            )
            .setPositiveButton(
                "Regresar"
            ) { _, _ ->

                finish()
            }
            .show()
    }

    private val editProductLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (
                result.resultCode ==
                RESULT_OK
            ) {

                val product =
                    EditProductActivity
                        .getUpdatedProduct(
                            result.data
                        )

                if (
                    product != null
                ) {

                    viewModel.applyUpdatedProduct(
                        product
                    )

                    Toast.makeText(
                        this@ProductDetailActivity,
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

    companion object {

        private const val EXTRA_PRODUCT_ID =
            "extra_product_id"

        private const val INVALID_PRODUCT_ID =
            -1

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
}