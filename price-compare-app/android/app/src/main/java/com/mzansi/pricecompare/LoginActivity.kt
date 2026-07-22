package com.mzansi.pricecompare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mzansi.pricecompare.api.RetrofitClient
import com.mzansi.pricecompare.databinding.ActivityLoginBinding
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        if (loading) return

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        loading = true
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Signing in..."

        scope.launch {
            try {
                val response = RetrofitClient.apiService.login(mapOf(
                    "email" to email,
                    "password" to password
                ))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val auth = response.body()
                        if (auth != null) {
                            val prefs = getSharedPreferences("mzansi_prefs", MODE_PRIVATE)
                            prefs.edit()
                                .putString("token", auth.token)
                                .putString("user_name", auth.user.name)
                                .putString("user_email", auth.user.email)
                                .putInt("user_id", auth.user.id)
                                .apply()

                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Login failed"
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    loading = false
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(R.string.login_btn)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
