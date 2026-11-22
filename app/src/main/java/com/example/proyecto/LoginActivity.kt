package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBinding: infla el layout y lo muestra
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta el contenido para no quedar bajo la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBiometricPrompt()

        binding.btnLogin.setOnClickListener {
            checkAndAuthenticate()
        }
    }

    // Configura el cuadro del sistema que pide la huella
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Huella correcta
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    SessionManager.isAuthenticated = true
                    startActivity(Intent(this@LoginActivity, ListaTareas::class.java))
                    finish() }

                // Error general: cancelación, bloqueo, etc.
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                            showMessage(R.string.login_error_canceled)

                        BiometricPrompt.ERROR_LOCKOUT ->
                            showMessage(R.string.login_error_lockout)

                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            showMessage(R.string.login_error_lockout_permanent)

                        else -> showMessage(R.string.login_error_generic)
                    }
                }

                // Huella que no coincide con ninguna registrada
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage(R.string.login_error_not_recognized)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.prompt_title))
            .setSubtitle(getString(R.string.prompt_subtitle))
            .setNegativeButtonText(getString(R.string.prompt_cancel))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    // Revisa si el celular puede usar huella antes de mostrar el cuadro
    private fun checkAndAuthenticate() {
        when (BiometricManager.from(this).canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricPrompt.authenticate(promptInfo)

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showMessage(R.string.login_error_no_hardware)

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showMessage(R.string.login_error_unavailable)

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                showMessage(R.string.login_error_none_enrolled)

            else -> showMessage(R.string.login_error_generic)
        }
    }

    private fun showMessage(@StringRes messageRes: Int) {
        binding.tvMessage.text = getString(messageRes)
    }
}