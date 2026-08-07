package com.mzansi.pricecompare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mzansi.pricecompare.R
import com.mzansi.pricecompare.api.models.ProductListing
import com.mzansi.pricecompare.databinding.ItemStoreListingBinding

class StoreListingAdapter(
    private val listings: List<ProductListing>
) : RecyclerView.Adapter<StoreListingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listings[position])
    }

    override fun getItemCount() = listings.size

    inner class ViewHolder(private val binding: ItemStoreListingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listing: ProductListing) {
            binding.tvStoreName.text = listing.store_name
            binding.tvPrice.text = String.format("%,.0f", listing.price)
            binding.tvDeliveryInfo.text = listing.delivery_info ?: ""
            binding.tvStoreLogo.text = listing.store_name.take(1)

            if (listing.price_was != null && listing.price_was > listing.price) {
                binding.tvPriceWas.visibility = android.view.View.VISIBLE
                binding.tvPriceWas.text = "Was R${String.format("%,.0f", listing.price_was)}"
            } else {
                binding.tvPriceWas.visibility = android.view.View.GONE
            }

            binding.btnBuy.setOnClickListener {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                intent.data = android.net.Uri.parse(listing.url)
                binding.root.context.startActivity(intent)
            }
        }
    }
}
