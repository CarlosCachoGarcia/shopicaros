package com.example.shopicaros.ui.audit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopicaros.R
import com.example.shopicaros.data.model.CartResponse

class CartAuditAdapter :
    ListAdapter<
            CartResponse,
            CartAuditAdapter.CartAuditViewHolder
            >(CartDiffCallback()) {

    private val expandedCarts =
        mutableSetOf<Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAuditViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_cart_audit,
                    parent,
                    false
                )

        return CartAuditViewHolder(
            view as ViewGroup
        )
    }

    override fun onBindViewHolder(
        holder: CartAuditViewHolder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }

    inner class CartAuditViewHolder(
        root: ViewGroup
    ) : RecyclerView.ViewHolder(root) {

        private val tvCartId: TextView =
            root.findViewById(
                R.id.tvAuditCartId
            )

        private val tvUserId: TextView =
            root.findViewById(
                R.id.tvAuditUserId
            )

        private val tvDate: TextView =
            root.findViewById(
                R.id.tvAuditDate
            )

        private val tvProductCount: TextView =
            root.findViewById(
                R.id.tvAuditProductCount
            )

        private val tvToggleDetails: TextView =
            root.findViewById(
                R.id.tvToggleAuditDetails
            )

        private val productsContainer:
                LinearLayout =
            root.findViewById(
                R.id.auditProductsContainer
            )

        fun bind(
            cart: CartResponse
        ) {

            tvCartId.text =
                "Carrito #${cart.id}"

            tvUserId.text =
                "Usuario ID: ${cart.userId}"

            tvDate.text =
                "Fecha: ${cart.date}"

            val totalQuantity =
                cart.products.sumOf {
                    it.quantity
                }

            tvProductCount.text =
                if (totalQuantity == 1) {
                    "1 artículo"
                } else {
                    "$totalQuantity artículos"
                }

            val isExpanded =
                expandedCarts.contains(
                    cart.id
                )

            renderProducts(
                cart,
                isExpanded
            )

            tvToggleDetails
                .setOnClickListener {

                    toggleCart(
                        cart
                    )
                }

            itemView
                .setOnClickListener {

                    toggleCart(
                        cart
                    )
                }
        }

        private fun toggleCart(
            cart: CartResponse
        ) {

            if (
                expandedCarts.contains(
                    cart.id
                )
            ) {

                expandedCarts.remove(
                    cart.id
                )

            } else {

                expandedCarts.add(
                    cart.id
                )
            }

            val position =
                bindingAdapterPosition

            if (
                position !=
                RecyclerView.NO_POSITION
            ) {

                notifyItemChanged(
                    position
                )
            }
        }

        private fun renderProducts(
            cart: CartResponse,
            isExpanded: Boolean
        ) {

            productsContainer.visibility =
                if (isExpanded) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            tvToggleDetails.text =
                if (isExpanded) {
                    "Ocultar detalles ▲"
                } else {
                    "Ver detalles ▼"
                }

            productsContainer.removeAllViews()

            if (!isExpanded) {
                return
            }

            if (
                cart.products.isEmpty()
            ) {

                val emptyText =
                    TextView(
                        productsContainer.context
                    ).apply {

                        text =
                            "Este carrito no contiene artículos."

                        textSize =
                            14f

                        setPadding(
                            0,
                            12,
                            0,
                            12
                        )
                    }

                productsContainer.addView(
                    emptyText
                )

                return
            }

            cart.products
                .forEachIndexed {
                        index,
                        product ->

                    val productText =
                        TextView(
                            productsContainer.context
                        ).apply {

                            text =
                                "Producto ID: ${product.productId}   •   Cantidad: ${product.quantity}"

                            textSize =
                                15f

                            setPadding(
                                0,
                                14,
                                0,
                                14
                            )
                        }

                    productsContainer.addView(
                        productText
                    )

                    if (
                        index <
                        cart.products.lastIndex
                    ) {

                        val divider =
                            View(
                                productsContainer.context
                            ).apply {

                                layoutParams =
                                    LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                    )

                                setBackgroundColor(
                                    0xFFE0E0E0.toInt()
                                )
                            }

                        productsContainer.addView(
                            divider
                        )
                    }
                }
        }
    }

    private class CartDiffCallback :
        DiffUtil.ItemCallback<CartResponse>() {

        override fun areItemsTheSame(
            oldItem: CartResponse,
            newItem: CartResponse
        ): Boolean {

            return oldItem.id ==
                    newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CartResponse,
            newItem: CartResponse
        ): Boolean {

            return oldItem ==
                    newItem
        }
    }
}