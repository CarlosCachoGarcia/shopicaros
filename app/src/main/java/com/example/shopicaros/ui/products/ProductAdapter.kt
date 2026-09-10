package com.example.shopicaros.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopicaros.R
import com.example.shopicaros.data.model.Product

class ProductAdapter(
    private val onProductClick: (Int) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(
    ProductDiffCallback()
) {

    class ProductViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.imgProduct)

        val title: TextView =
            view.findViewById(R.id.tvTitle)

        val price: TextView =
            view.findViewById(R.id.tvPrice)

        val category: TextView =
            view.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_product,
                    parent,
                    false
                )

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product =
            getItem(position)

        holder.title.text =
            product.title

        holder.price.text =
            "$${String.format("%.2f", product.price)}"

        holder.category.text =
            product.category

        Glide
            .with(holder.image.context)
            .load(product.image)
            .fitCenter()
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onProductClick(product.id)
        }
    }
}

private class ProductDiffCallback :
    DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(
        oldItem: Product,
        newItem: Product
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Product,
        newItem: Product
    ): Boolean {
        return oldItem == newItem
    }
}