package com.example.shopicaros.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.R
import com.example.shopicaros.data.local.SharedPreferencesCartLocalDataSource
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.CartRepository
import com.example.shopicaros.data.repository.CartRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton
import java.util.Locale
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var emptyCartContainer: LinearLayout
    private lateinit var progressCart: ProgressBar

    private lateinit var tvCartTotal: TextView
    private lateinit var btnProceedPayment: MaterialButton

    private val sessionRepository: UserSessionRepository by lazy {

        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val cartRepository: CartRepository by lazy {

        CartRepositoryImpl(
            RetrofitClient.api,
            SharedPreferencesCartLocalDataSource(
                applicationContext
            )
        )
    }

    private val viewModel: CartViewModel by viewModels {

        CartViewModelFactory(
            cartRepository,
            sessionRepository
        )
    }

    private val cartAdapter by lazy {

        CartAdapter(

            onIncrease = { item ->

                viewModel.changeQuantity(
                    productId =
                        item.productId,

                    newQuantity =
                        item.quantity + 1
                )
            },

            onDecrease = { item ->

                viewModel.changeQuantity(
                    productId =
                        item.productId,

                    newQuantity =
                        item.quantity - 1
                )
            },

            onDelete = { item ->

                viewModel.removeProduct(
                    item.productId
                )
            }
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        if (
            !sessionRepository.isLoggedIn()
        ) {

            goToLogin()
            return
        }

        if (
            sessionRepository.getRole() !=
            UserRole.CLIENTE
        ) {

            Toast.makeText(
                this,
                "El carrito está disponible únicamente para Cliente.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setContentView(
            R.layout.activity_cart
        )

        bindViews()

        setupRecyclerView()

        setupActions()

        observeState()

        observeEvents()

        viewModel.loadCart()
    }

    private fun bindViews() {

        recyclerCart =
            findViewById(
                R.id.recyclerCart
            )

        emptyCartContainer =
            findViewById(
                R.id.emptyCartContainer
            )

        progressCart =
            findViewById(
                R.id.progressCart
            )

        tvCartTotal =
            findViewById(
                R.id.tvCartTotal
            )

        btnProceedPayment =
            findViewById(
                R.id.btnProceedPayment
            )
    }

    private fun setupRecyclerView() {

        recyclerCart.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerCart.adapter =
            cartAdapter
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvCartBack
        ).setOnClickListener {

            finish()
        }

        btnProceedPayment
            .setOnClickListener {

                Toast.makeText(
                    this,
                    "Simulación de pago",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun observeState() {

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

    private fun observeEvents() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.events
                    .collect { event ->

                        when (event) {

                            is CartEvent.ProductAdded -> {
                                // Se utiliza en detalle de producto.
                            }

                            is CartEvent.ShowMessage -> {

                                Toast.makeText(
                                    this@CartActivity,
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
        state: CartUiState
    ) {

        cartAdapter.submitList(
            state.items
        )

        val isEmpty =
            state.items.isEmpty()

        recyclerCart.visibility =
            if (isEmpty) {
                View.GONE
            } else {
                View.VISIBLE
            }

        emptyCartContainer.visibility =
            if (isEmpty) {
                View.VISIBLE
            } else {
                View.GONE
            }

        progressCart.visibility =
            if (state.isUpdating) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvCartTotal.text =
            String.format(
                Locale.US,
                "Total: $%.2f",
                state.total
            )

        btnProceedPayment.isEnabled =
            !isEmpty &&
                    !state.isUpdating
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
}