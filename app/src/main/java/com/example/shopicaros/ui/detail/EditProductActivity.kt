package com.example.shopicaros.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shopicaros.R
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.data.repository.ProductRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EditProductActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etDescription: TextInputEditText

    private lateinit var progressEdit: ProgressBar
    private lateinit var btnSaveProduct: MaterialButton

    private var formLoaded = false

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

    private val viewModel: EditProductViewModel by viewModels {
        EditProductViewModelFactory(
            productRepository,
            sessionRepository
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        // US07 - Debe existir una sesión válida.
        if (!sessionRepository.isLoggedIn()) {
            goToLogin()
            return
        }

        // US07 - Solamente Administrador puede editar.
        if (
            sessionRepository.getRole() !=
            UserRole.ADMINISTRADOR
        ) {

            Toast.makeText(
                this,
                "No tienes permisos para editar productos.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setContentView(
            R.layout.activity_edit_product
        )

        bindViews()
        setupActions()
        observeState()
        observeEvents()

        val productId =
            intent.getIntExtra(
                EXTRA_PRODUCT_ID,
                -1
            )

        if (productId <= 0) {

            Toast.makeText(
                this,
                "Producto no válido.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        viewModel.loadProduct(
            productId
        )
    }

    private fun bindViews() {

        etTitle =
            findViewById(R.id.etTitle)

        etPrice =
            findViewById(R.id.etPrice)

        etCategory =
            findViewById(R.id.etCategory)

        etDescription =
            findViewById(R.id.etDescription)

        progressEdit =
            findViewById(R.id.progressEdit)

        btnSaveProduct =
            findViewById(R.id.btnSaveProduct)
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvEditBack
        ).setOnClickListener {

            finish()
        }

        btnSaveProduct.setOnClickListener {

            viewModel.saveChanges(
                title =
                    etTitle.text.toString(),

                priceText =
                    etPrice.text.toString(),

                category =
                    etCategory.text.toString(),

                description =
                    etDescription.text.toString()
            )
        }
    }

    private fun observeState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    progressEdit.visibility =
                        if (
                            state.isLoading ||
                            state.isSaving
                        ) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    btnSaveProduct.isEnabled =
                        !state.isSaving

                    if (
                        state.product != null &&
                        !formLoaded
                    ) {

                        formLoaded = true

                        fillForm(
                            state.product
                        )
                    }
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

                        is EditProductEvent.Saved -> {

                            setResult(
                                RESULT_OK,
                                createResultIntent(
                                    event.product
                                )
                            )

                            finish()
                        }

                        is EditProductEvent.ShowMessage -> {

                            Toast.makeText(
                                this@EditProductActivity,
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun fillForm(
        product: Product
    ) {

        etTitle.setText(
            product.title
        )

        etPrice.setText(
            product.price.toString()
        )

        etCategory.setText(
            product.category
        )

        etDescription.setText(
            product.description
        )
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

    companion object {

        private const val EXTRA_PRODUCT_ID =
            "edit_product_id"

        private const val RESULT_ID =
            "updated_id"

        private const val RESULT_TITLE =
            "updated_title"

        private const val RESULT_PRICE =
            "updated_price"

        private const val RESULT_DESCRIPTION =
            "updated_description"

        private const val RESULT_CATEGORY =
            "updated_category"

        private const val RESULT_IMAGE =
            "updated_image"

        fun createIntent(
            context: Context,
            productId: Int
        ): Intent {

            return Intent(
                context,
                EditProductActivity::class.java
            ).apply {

                putExtra(
                    EXTRA_PRODUCT_ID,
                    productId
                )
            }
        }

        private fun createResultIntent(
            product: Product
        ): Intent {

            return Intent().apply {

                putExtra(
                    RESULT_ID,
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

        fun getUpdatedProduct(
            intent: Intent?
        ): Product? {

            if (intent == null) {
                return null
            }

            val id =
                intent.getIntExtra(
                    RESULT_ID,
                    -1
                )

            if (id <= 0) {
                return null
            }

            return Product(
                id = id,

                title =
                    intent.getStringExtra(
                        RESULT_TITLE
                    ).orEmpty(),

                price =
                    intent.getDoubleExtra(
                        RESULT_PRICE,
                        0.0
                    ),

                description =
                    intent.getStringExtra(
                        RESULT_DESCRIPTION
                    ).orEmpty(),

                category =
                    intent.getStringExtra(
                        RESULT_CATEGORY
                    ).orEmpty(),

                image =
                    intent.getStringExtra(
                        RESULT_IMAGE
                    ).orEmpty()
            )
        }
    }
}