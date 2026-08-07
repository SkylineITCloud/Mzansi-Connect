package com.mzansi.pricecompare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mzansi.pricecompare.api.RetrofitClient
import com.mzansi.pricecompare.databinding.ActivityRegisterBinding
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener { finish() }
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        if (loading) return

        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        loading = true
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Creating account..."

        scope.launch {
            try {
                val response = RetrofitClient.apiService.register(mapOf(
                    "name" to name,
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

                            Toast.makeText(this@RegisterActivity, "Account created! 🇿🇦", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        val error = response.errorBody()?.string() ?: "Registration failed"
                        Toast.makeText(this@RegisterActivity, "Email already registered", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    loading = false
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = getString(R.string.register_btn)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
