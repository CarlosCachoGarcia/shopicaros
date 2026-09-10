package com.example.shopicaros.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shopicaros.MainActivity
import com.example.shopicaros.R
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.data.repository.ProductRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import android.widget.ImageButton
class AddProductActivity : AppCompatActivity() {

    private lateinit var inputTitle: TextInputLayout
    private lateinit var inputPrice: TextInputLayout
    private lateinit var inputDescription: TextInputLayout
    private lateinit var inputCategory: TextInputLayout
    private lateinit var inputImageUrl: TextInputLayout

    private lateinit var etTitle: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etImageUrl: TextInputEditText

    private lateinit var btnSaveProduct: MaterialButton
    private lateinit var progressAddProduct: ProgressBar
    private lateinit var tvGeneralError: TextView
    private lateinit var btnBack: ImageButton
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

    private val viewModel: AddProductViewModel by viewModels {
        AddProductViewModelFactory(
            productRepository,
            sessionRepository
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        if (!sessionRepository.isLoggedIn()) {
            goToLogin()
            return
        }

        if (
            sessionRepository.getRole() !=
            UserRole.ADMINISTRADOR
        ) {
            Toast.makeText(
                this,
                "Acceso denegado.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setContentView(
            R.layout.activity_add_product
        )

        bindViews()
        setupActions()
        observeUiState()
        observeEvents()
    }

    private fun bindViews() {

        inputTitle =
            findViewById(R.id.inputTitle)

        inputPrice =
            findViewById(R.id.inputPrice)

        inputDescription =
            findViewById(R.id.inputDescription)

        inputCategory =
            findViewById(R.id.inputCategory)

        inputImageUrl =
            findViewById(R.id.inputImageUrl)

        etTitle =
            findViewById(R.id.etTitle)

        etPrice =
            findViewById(R.id.etPrice)

        etDescription =
            findViewById(R.id.etDescription)

        etCategory =
            findViewById(R.id.etCategory)

        etImageUrl =
            findViewById(R.id.etImageUrl)

        btnSaveProduct =
            findViewById(R.id.btnSaveProduct)

        progressAddProduct =
            findViewById(R.id.progressAddProduct)

        tvGeneralError =
            findViewById(R.id.tvGeneralError)
        btnBack =
            findViewById(R.id.btnBack)
    }

    private fun setupActions() {

        btnSaveProduct.setOnClickListener {

            viewModel.saveProduct(
                title =
                    etTitle.text.toString(),
                priceText =
                    etPrice.text.toString(),
                description =
                    etDescription.text.toString(),
                category =
                    etCategory.text.toString(),
                imageUrl =
                    etImageUrl.text.toString()
            )
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeUiState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    progressAddProduct.visibility =
                        if (state.isLoading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    btnSaveProduct.isEnabled =
                        !state.isLoading

                    inputTitle.error =
                        state.titleError

                    inputPrice.error =
                        state.priceError

                    inputDescription.error =
                        state.descriptionError

                    inputCategory.error =
                        state.categoryError

                    inputImageUrl.error =
                        state.imageUrlError

                    if (state.generalError != null) {

                        tvGeneralError.text =
                            state.generalError

                        tvGeneralError.visibility =
                            View.VISIBLE

                    } else {

                        tvGeneralError.visibility =
                            View.GONE
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

                        is AddProductEvent.ProductCreated -> {

                            clearForm()

                            AlertDialog.Builder(
                                this@AddProductActivity
                            )
                                .setTitle(
                                    "Producto registrado"
                                )
                                .setMessage(
                                    "Producto creado correctamente.\n" +
                                            "ID asignado: ${event.product.id}\n\n" +
                                            "Operación simulada por Fake Store API."
                                )
                                .setPositiveButton(
                                    "Aceptar"
                                ) { _, _ ->
                                    finish()
                                }
                                .setCancelable(false)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun clearForm() {

        etTitle.text?.clear()
        etPrice.text?.clear()
        etDescription.text?.clear()
        etCategory.text?.clear()
        etImageUrl.text?.clear()
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
}