package com.mzansi.pricecompare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mzansi.pricecompare.adapter.CategoryAdapter
import com.mzansi.pricecompare.adapter.ProductAdapter
import com.mzansi.pricecompare.adapter.TrendingAdapter
import com.mzansi.pricecompare.api.RetrofitClient
import com.mzansi.pricecompare.databinding.ActivityDashboardBinding
import kotlinx.coroutines.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerViews()
        loadData()

        binding.cardSearch.setOnClickListener {
            startActivity(Intent(this, SearchResultsActivity::class.java))
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchResultsActivity::class.java))
                    true
                }
                R.id.nav_saved -> {
                    Toast.makeText(this, "Saved items coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerViews() {
        productAdapter = ProductAdapter(mutableListOf(), this)
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = productAdapter

        binding.rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadData() {
        scope.launch {
            try {
                // Load products
                val productsResponse = RetrofitClient.apiService.getProducts()
                withContext(Dispatchers.Main) {
                    productAdapter.updateList(productsResponse.body()?.products ?: emptyList())
                }

                // Load categories
                val categoriesResponse = RetrofitClient.apiService.getCategories()
                withContext(Dispatchers.Main) {
                    val categories = categoriesResponse.body()?.categories ?: emptyList()
                    binding.rvCategories.adapter = CategoryAdapter(categories)
                }

                // Load trending
                val trendingResponse = RetrofitClient.apiService.getTrending()
                withContext(Dispatchers.Main) {
                    val trending = trendingResponse.body()?.trending?.map { it.query } ?: emptyList()
                    binding.rvTrending.adapter = TrendingAdapter(trending)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DashboardActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
