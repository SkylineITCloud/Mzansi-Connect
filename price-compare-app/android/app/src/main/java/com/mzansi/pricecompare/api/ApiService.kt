package com.mzansi.pricecompare.api

import com.mzansi.pricecompare.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserResponse>

    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("category") category: String? = null
    ): Response<ProductsResponse>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): Response<ProductResponse>

    @GET("api/products/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("api/products/saved/list")
    suspend fun getSavedProducts(@Header("Authorization") token: String): Response<SavedResponse>

    @POST("api/products/{id}/save")
    suspend fun saveProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MessageResponse>

    @DELETE("api/products/{id}/save")
    suspend fun unsaveProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MessageResponse>

    @GET("api/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("sort") sort: String = "price_asc",
        @Query("page") page: Int = 1
    ): Response<SearchResponse>

    @GET("api/search/trending")
    suspend fun getTrending(): Response<TrendingResponse>
}
