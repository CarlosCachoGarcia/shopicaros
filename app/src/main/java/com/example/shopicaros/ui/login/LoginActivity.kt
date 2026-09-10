package com.example.shopicaros.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shopicaros.MainActivity
import com.example.shopicaros.R
import com.example.shopicaros.data.remote.RetrofitClient
import com.example.shopicaros.data.repository.AuthRepository
import com.example.shopicaros.data.repository.AuthRepositoryImpl
import com.example.shopicaros.session.SharedPreferencesUserSessionRepository
import com.example.shopicaros.session.UserSessionRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressLogin: ProgressBar
    private lateinit var tvLoginError: TextView

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            RetrofitClient.api
        )
    }

    private val sessionRepository: UserSessionRepository by lazy {
        SharedPreferencesUserSessionRepository(
            applicationContext
        )
    }

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            authRepository,
            sessionRepository
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        /*
         * Si ya existe un token guardado,
         * no mostramos nuevamente el login.
         */
        if (sessionRepository.isLoggedIn()) {
            openMainActivity()
            return
        }

        setContentView(
            R.layout.activity_login
        )

        bindViews()
        setupActions()
        observeUiState()
        observeEvents()
    }

    private fun bindViews() {

        etUsername =
            findViewById(R.id.etUsername)

        etPassword =
            findViewById(R.id.etPassword)

        btnLogin =
            findViewById(R.id.btnLogin)

        progressLogin =
            findViewById(R.id.progressLogin)

        tvLoginError =
            findViewById(R.id.tvLoginError)
    }

    private fun setupActions() {

        btnLogin.setOnClickListener {

            viewModel.onUsernameChanged(
                etUsername.text.toString()
            )

            viewModel.onPasswordChanged(
                etPassword.text.toString()
            )

            viewModel.login()
        }
    }

    private fun observeUiState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    progressLogin.visibility =
                        if (state.isLoading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    btnLogin.isEnabled =
                        !state.isLoading

                    etUsername.isEnabled =
                        !state.isLoading

                    etPassword.isEnabled =
                        !state.isLoading

                    val error =
                        state.errorMessage

                    if (error != null) {

                        tvLoginError.text =
                            error

                        tvLoginError.visibility =
                            View.VISIBLE

                    } else {

                        tvLoginError.visibility =
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

                        LoginEvent.LoginSuccess -> {
                            openMainActivity()
                        }
                    }
                }
            }
        }
    }

    private fun openMainActivity() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}