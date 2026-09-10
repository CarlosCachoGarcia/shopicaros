package com.example.shopicaros.ui.audit

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
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.CartAuditRepository
import com.example.shopicaros.data.repository.CartAuditRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CartAuditActivity :
    AppCompatActivity() {

    private lateinit var recyclerCartAudit:
            RecyclerView

    private lateinit var progressCartAudit:
            ProgressBar

    private lateinit var auditErrorContainer:
            LinearLayout

    private lateinit var tvAuditError:
            TextView

    private lateinit var btnAuditRetry:
            MaterialButton

    private lateinit var tvAuditEmpty:
            TextView

    private val sessionRepository:
            UserSessionRepository by lazy {

        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val repository:
            CartAuditRepository by lazy {

        CartAuditRepositoryImpl(
            RetrofitClient.api
        )
    }

    private val viewModel:
            CartAuditViewModel by viewModels {

        CartAuditViewModelFactory(
            repository,
            sessionRepository
        )
    }

    private val auditAdapter by lazy {

        CartAuditAdapter()
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

        val role =
            sessionRepository.getRole()

        if (
            role != UserRole.ADMINISTRADOR &&
            role != UserRole.AUDITOR
        ) {

            Toast.makeText(
                this,
                "No tienes permisos para consultar la auditoría.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setContentView(
            R.layout.activity_cart_audit
        )

        bindViews()
        setupRecyclerView()
        setupActions()
        observeState()

        viewModel.loadCarts()
    }

    private fun bindViews() {

        recyclerCartAudit =
            findViewById(
                R.id.recyclerCartAudit
            )

        progressCartAudit =
            findViewById(
                R.id.progressCartAudit
            )

        auditErrorContainer =
            findViewById(
                R.id.auditErrorContainer
            )

        tvAuditError =
            findViewById(
                R.id.tvAuditError
            )

        btnAuditRetry =
            findViewById(
                R.id.btnAuditRetry
            )

        tvAuditEmpty =
            findViewById(
                R.id.tvAuditEmpty
            )
    }

    private fun setupRecyclerView() {

        recyclerCartAudit.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerCartAudit.adapter =
            auditAdapter
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvAuditBack
        ).setOnClickListener {

            finish()
        }

        btnAuditRetry
            .setOnClickListener {

                viewModel.retry()
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

    private fun render(
        state: CartAuditUiState
    ) {

        val hasError =
            state.errorMessage != null

        val isEmpty =
            state.carts.isEmpty() &&
                    !state.isLoading &&
                    !hasError

        progressCartAudit.visibility =
            if (state.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        auditErrorContainer.visibility =
            if (
                hasError &&
                !state.isLoading
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvAuditEmpty.visibility =
            if (isEmpty) {
                View.VISIBLE
            } else {
                View.GONE
            }

        recyclerCartAudit.visibility =
            if (
                !state.isLoading &&
                !hasError &&
                !isEmpty
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvAuditError.text =
            state.errorMessage
                ?: "No fue posible cargar el historial de carritos."

        auditAdapter.submitList(
            state.carts
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

        startActivity(
            intent
        )

        finish()
    }
}