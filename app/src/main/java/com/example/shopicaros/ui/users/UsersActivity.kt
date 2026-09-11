package com.example.shopicaros.ui.users

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
import com.example.shopicaros.data.repository.UserRepository
import com.example.shopicaros.data.repository.UserRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import com.example.shopicaros.ui.login.LoginActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class UsersActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var progressUsers: ProgressBar

    private lateinit var usersErrorContainer: LinearLayout
    private lateinit var tvUsersError: TextView
    private lateinit var btnUsersRetry: MaterialButton

    private val sessionRepository:
            UserSessionRepository by lazy {

        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val userRepository:
            UserRepository by lazy {

        UserRepositoryImpl(
            RetrofitClient.api
        )
    }

    private val viewModel:
            UsersViewModel by viewModels {

        UsersViewModelFactory(
            userRepository,
            sessionRepository
        )
    }

    private val usersAdapter by lazy {

        UsersAdapter()
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
                "No tienes permisos para consultar usuarios.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setContentView(
            R.layout.activity_users
        )

        bindViews()

        setupRecyclerView()

        setupActions()

        observeState()

        viewModel.loadUsers()
    }

    private fun bindViews() {

        recyclerUsers =
            findViewById(
                R.id.recyclerUsers
            )

        progressUsers =
            findViewById(
                R.id.progressUsers
            )

        usersErrorContainer =
            findViewById(
                R.id.usersErrorContainer
            )

        tvUsersError =
            findViewById(
                R.id.tvUsersError
            )

        btnUsersRetry =
            findViewById(
                R.id.btnUsersRetry
            )
    }

    private fun setupRecyclerView() {

        recyclerUsers.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerUsers.adapter =
            usersAdapter
    }

    private fun setupActions() {

        findViewById<TextView>(
            R.id.tvUsersBack
        ).setOnClickListener {

            finish()
        }

        btnUsersRetry
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
        state: UsersUiState
    ) {

        progressUsers.visibility =
            if (state.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        val hasError =
            state.errorMessage != null

        usersErrorContainer.visibility =
            if (
                hasError &&
                !state.isLoading
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        recyclerUsers.visibility =
            if (
                !state.isLoading &&
                !hasError
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvUsersError.text =
            state.errorMessage
                ?: "No fue posible cargar los usuarios."

        usersAdapter.submitList(
            state.users
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