package com.mzansi.pricecompare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mzansi.pricecompare.adapter.StoreListingAdapter
import com.mzansi.pricecompare.api.RetrofitClient
import com.mzansi.pricecompare.databinding.ActivityProductDetailBinding
import kotlinx.coroutines.*

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        productId = intent.getIntExtra("product_id", 0)
        if (productId == 0) { finish(); return }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveProduct() }

        loadProductDetail()
    }

    private fun loadProductDetail() {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getProductDetail(productId)
                withContext(Dispatchers.Main) {
                    val product = response.body()?.product
                    if (product != null) {
                        binding.tvBrand.text = product.brand ?: ""
                        binding.tvProductName.text = product.name
                        binding.tvDescription.text = product.description

                        val low = product.lowest_price ?: 0.0
                        val high = product.highest_price ?: 0.0
                        if (low == high) {
                            binding.tvPriceRange.text = "R${String.format("%,.0f", low)}"
                        } else {
                            binding.tvPriceRange.text = "R${String.format("%,.0f", low)} — R${String.format("%,.0f", high)}"
                        }

                        if (!product.image_url.isNullOrEmpty()) {
                            Glide.with(this@ProductDetailActivity)
                                .load(product.image_url)
                                .placeholder(R.color.green_light)
                                .into(binding.ivProduct)
                        }

                        // Store listings
                        val listings = product.listings
                        if (listings != null && listings.isNotEmpty()) {
                            binding.rvStoreListings.layoutManager = LinearLayoutManager(this@ProductDetailActivity)
                            binding.rvStoreListings.adapter = StoreListingAdapter(listings)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProduct() {
        val prefs = getSharedPreferences("mzansi_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null) ?: return

        scope.launch {
            try {
                val response = RetrofitClient.apiService.saveProduct("Bearer $token", productId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProductDetailActivity, "Saved! 🇿🇦", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductDetailActivity, "Please login to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
