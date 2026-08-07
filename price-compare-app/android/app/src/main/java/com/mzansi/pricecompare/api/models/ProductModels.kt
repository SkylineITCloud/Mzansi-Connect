package com.mzansi.pricecompare.api.models

data class ProductListing(
    val id: Int,
    val product_id: Int,
    val store_name: String,
    val store_logo: String,
    val price: Double,
    val price_was: Double?,
    val url: String,
    val in_stock: Int,
    val delivery_info: String?,
    val rating: Double?
)

data class Product(
    val id: Int,
    val name: String,
    val brand: String?,
    val category: String,
    val image_url: String?,
    val description: String?,
    val lowest_price: Double?,
    val highest_price: Double?,
    val store_count: Int?,
    val listings: List<ProductListing>? = null
)

data class ProductResponse(
    val product: Product
)

data class ProductsResponse(
    val products: List<Product>,
    val total: Int,
    val page: Int,
    val pages: Int
)

data class SearchResponse(
    val products: List<Product>,
    val total: Int,
    val page: Int,
    val pages: Int,
    val query: String
)

data class SavedResponse(
    val products: List<Product>
)

data class CategoriesResponse(
    val categories: List<String>
)

data class TrendingResponse(
    val trending: List<TrendingItem>
)

data class TrendingItem(
    val query: String,
    val count: Int
)

data class MessageResponse(
    val message: String
)
