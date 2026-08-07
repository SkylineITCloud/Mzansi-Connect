package com.mzansi.pricecompare.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mzansi.pricecompare.ProductDetailActivity
import com.mzansi.pricecompare.R
import com.mzansi.pricecompare.api.models.Product
import com.mzansi.pricecompare.databinding.ItemProductBinding

class ProductAdapter(
    private val products: MutableList<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = products.size

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvBrand.text = product.brand ?: ""
            binding.tvProductName.text = product.name
            binding.tvPrice.text = String.format("%,.0f", product.lowest_price ?: 0.0)
            binding.tvStoreCount.text = String.format(
                binding.root.context.getString(R.string.stores_count),
                product.store_count ?: 0
            )
            if (!product.image_url.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(product.image_url)
                    .placeholder(R.color.green_light)
                    .into(binding.ivProduct)
            }
        }
    }
}
