package com.example.shopicaros.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.R
import com.example.shopicaros.data.model.Product

class ProductAdapter(
    private var products: List<Product> = emptyList()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.tvTitle)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val category: TextView = view.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = products[position]

        holder.title.text = product.title
        holder.price.text = "$${String.format("%.2f", product.price)}"
        holder.category.text = product.category
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun clearProducts() {
        products = emptyList()
        notifyDataSetChanged()
    }
}