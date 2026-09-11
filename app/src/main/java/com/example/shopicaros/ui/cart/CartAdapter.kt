package com.example.shopicaros.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopicaros.R
import com.example.shopicaros.data.model.CartItem
import com.google.android.material.button.MaterialButton
import android.widget.ImageView
import android.widget.TextView
import java.util.Locale

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : ListAdapter<
        CartItem,
        CartAdapter.CartViewHolder
        >(CartDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_cart,
                    parent,
                    false
                )

        return CartViewHolder(
            view as ViewGroup
        )
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }

    inner class CartViewHolder(
        root: ViewGroup
    ) : RecyclerView.ViewHolder(root) {

        private val ivProduct: ImageView =
            root.findViewById(
                R.id.ivCartProduct
            )

        private val tvTitle: TextView =
            root.findViewById(
                R.id.tvCartTitle
            )

        private val tvPrice: TextView =
            root.findViewById(
                R.id.tvCartPrice
            )

        private val tvQuantity: TextView =
            root.findViewById(
                R.id.tvCartQuantity
            )

        private val tvSubtotal: TextView =
            root.findViewById(
                R.id.tvCartSubtotal
            )

        private val btnDecrease: MaterialButton =
            root.findViewById(
                R.id.btnCartDecrease
            )

        private val btnIncrease: MaterialButton =
            root.findViewById(
                R.id.btnCartIncrease
            )

        private val btnDelete: MaterialButton =
            root.findViewById(
                R.id.btnRemoveCartItem
            )

        fun bind(
            item: CartItem
        ) {

            tvTitle.text =
                item.title

            tvPrice.text =
                String.format(
                    Locale.US,
                    "$%.2f",
                    item.price
                )

            tvQuantity.text =
                item.quantity.toString()

            tvSubtotal.text =
                String.format(
                    Locale.US,
                    "Subtotal: $%.2f",
                    item.price *
                            item.quantity
                )

            Glide
                .with(ivProduct.context)
                .load(item.image)
                .fitCenter()
                .into(ivProduct)

            btnIncrease.setOnClickListener {

                onIncrease(
                    item
                )
            }

            btnDecrease.setOnClickListener {

                onDecrease(
                    item
                )
            }

            btnDelete.setOnClickListener {

                onDelete(
                    item
                )
            }
        }
    }

    private class CartDiffCallback :
        DiffUtil.ItemCallback<CartItem>() {

        override fun areItemsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {

            return oldItem.productId ==
                    newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {

            return oldItem ==
                    newItem
        }
    }
}