package com.mzansi.pricecompare.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mzansi.pricecompare.SearchResultsActivity
import com.mzansi.pricecompare.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<String>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategory.text = category
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SearchResultsActivity::class.java)
            intent.putExtra("query", category)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = categories.size

    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
