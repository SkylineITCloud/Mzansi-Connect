package com.mzansi.pricecompare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mzansi.pricecompare.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val prefs = getSharedPreferences("mzansi_prefs", MODE_PRIVATE)
        val name = prefs.getString("user_name", "Mzansi User")
        val email = prefs.getString("user_email", "")

        binding.tvName.text = name
        binding.tvEmail.text = email

        binding.btnBack.setOnClickListener { finish() }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchResultsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.root.setOnLongClickListener {
            prefs.edit().clear().apply()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
    }
}
