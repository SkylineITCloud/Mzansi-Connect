package com.mzansi.pricecompare

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mzansi.pricecompare.adapter.ProductAdapter
import com.mzansi.pricecompare.api.RetrofitClient
import com.mzansi.pricecompare.databinding.ActivitySearchResultsBinding
import kotlinx.coroutines.*
import java.util.Locale

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var productAdapter: ProductAdapter
    private var currentQuery: String = ""
    private var currentSort: String = "price_asc"

    companion object {
        private const val SPEECH_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        currentQuery = intent.getStringExtra("query") ?: ""

        productAdapter = ProductAdapter(mutableListOf(), this)
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = productAdapter

        binding.btnBack.setOnClickListener { finish() }

        setupSearch()
        setupSortChips()
    }

    private fun setupSearch() {
        val searchView = androidx.appcompat.widget.SearchView(this)
        // Add search view to toolbar or handle via dialog
        showSearchDialog()
    }

    private fun showSearchDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Search products")

        val input = android.widget.EditText(this).apply {
            hint = "e.g. iPhone, sneakers, laptop..."
            setText(currentQuery)
            setPadding(32, 24, 32, 24)
        }

        builder.setView(input)

        builder.setPositiveButton("Search") { _, _ ->
            val query = input.text.toString().trim()
            if (query.isNotEmpty()) {
                currentQuery = query
                performSearch()
            }
        }

        builder.setNegativeButton("Cancel", null)

        builder.setNeutralButton("🎤 Voice") { _, _ ->
            startVoiceSearch()
        }

        builder.show()
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a product to search")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice search not supported", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Use registerForActivityResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                currentQuery = results[0]
                performSearch()
            }
        }
    }

    private fun setupSortChips() {
        binding.chipLowPrice.setOnClickListener {
            currentSort = "price_asc"
            performSearch()
        }
        binding.chipHighPrice.setOnClickListener {
            currentSort = "price_desc"
            performSearch()
        }
    }

    private fun performSearch() {
        if (currentQuery.isEmpty()) return

        binding.tvQuery.text = "Searching \"$currentQuery\"..."
        scope.launch {
            try {
                val response = RetrofitClient.apiService.search(currentQuery, currentSort)
                withContext(Dispatchers.Main) {
                    val body = response.body()
                    if (body != null && body.products.isNotEmpty()) {
                        productAdapter.updateList(body.products)
                        binding.tvResultCount.text = "(${body.total} found)"
                        binding.tvQuery.text = "\"${body.query}\""
                    } else {
                        productAdapter.updateList(emptyList())
                        binding.tvResultCount.text = "(0 found)"
                        binding.tvQuery.text = "No results for \"$currentQuery\""
                        Toast.makeText(this@SearchResultsActivity, "No products found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SearchResultsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
