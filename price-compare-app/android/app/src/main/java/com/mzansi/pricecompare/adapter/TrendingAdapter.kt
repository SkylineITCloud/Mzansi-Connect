package com.mzansi.pricecompare.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mzansi.pricecompare.SearchResultsActivity
import com.mzansi.pricecompare.databinding.ItemTrendingBinding

class TrendingAdapter(
    private val queries: List<String>
) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val query = queries[position]
        holder.binding.tvTrending.text = "🔥 $query"
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SearchResultsActivity::class.java)
            intent.putExtra("query", query)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = queries.size

    inner class ViewHolder(val binding: ItemTrendingBinding) :
        RecyclerView.ViewHolder(binding.root)
}
