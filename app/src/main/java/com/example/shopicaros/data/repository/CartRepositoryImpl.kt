package com.example.shopicaros.data.repository

import com.example.shopicaros.data.local.CartLocalDataSource
import com.example.shopicaros.data.model.CartItem
import com.example.shopicaros.data.model.CartProductRequest
import com.example.shopicaros.data.model.CartRequest
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.remote.FakeStoreApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartRepositoryImpl(
    private val api: FakeStoreApi,
    private val localDataSource: CartLocalDataSource
) : CartRepository {

    override suspend fun addProductToCart(
        userId: Int,
        product: Product,
        quantity: Int
    ): CartItem {

        val request =
            createRequest(
                userId = userId,
                products =
                    listOf(
                        CartProductRequest(
                            productId = product.id,
                            quantity = quantity
                        )
                    )
            )

        try {

            val response =
                api.addCart(
                    request
                )

            if (response.id > 0) {

                localDataSource.saveCartId(
                    response.id
                )
            }

        } catch (_: Exception) {

            /*
             * Fake Store API solo simula las escrituras.
             * El carrito local sigue siendo la fuente
             * real de datos de la aplicación.
             */
        }

        val localItem =
            CartItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                image = product.image,
                quantity = quantity
            )

        return localDataSource.addOrUpdate(
            localItem
        )
    }

    override suspend fun updateProductQuantity(
        userId: Int,
        productId: Int,
        quantity: Int
    ): List<CartItem> {

        if (quantity <= 0) {

            return removeProduct(
                userId = userId,
                productId = productId
            )
        }

        val currentItems =
            localDataSource.getItems()

        val updatedItems =
            currentItems.map { item ->

                if (
                    item.productId ==
                    productId
                ) {

                    item.copy(
                        quantity = quantity
                    )

                } else {

                    item
                }
            }

        /*
         * Intentamos simular el PUT.
         * Si Fake Store no conserva el carrito
         * creado anteriormente, no bloqueamos
         * la actualización local.
         */
        try {

            val cartId =
                ensureCartId(
                    userId = userId,
                    items = updatedItems
                )

            val request =
                createRequestFromItems(
                    userId = userId,
                    items = updatedItems
                )

            api.updateCart(
                cartId = cartId,
                request = request
            )

        } catch (_: Exception) {

            // La actualización continuará localmente.
        }

        return localDataSource.updateQuantity(
            productId = productId,
            quantity = quantity
        )
    }

    override suspend fun removeProduct(
        userId: Int,
        productId: Int
    ): List<CartItem> {

        val currentItems =
            localDataSource.getItems()

        /*
         * Intentamos enviar DELETE a Fake Store.
         *
         * Como Fake Store API no persiste realmente
         * los carritos creados mediante POST,
         * DELETE puede responder con error.
         *
         * Eso no debe impedir eliminar el producto
         * del carrito local.
         */
        try {

            val cartId =
                ensureCartId(
                    userId = userId,
                    items = currentItems
                )

            api.deleteCart(
                cartId
            )

        } catch (_: Exception) {

            // Continuamos con el borrado local.
        }

        return localDataSource.removeItem(
            productId
        )
    }

    override fun getLocalItems(): List<CartItem> {

        return localDataSource.getItems()
    }

    override fun clearLocalCart() {

        localDataSource.clear()
    }

    private suspend fun ensureCartId(
        userId: Int,
        items: List<CartItem>
    ): Int {

        val existingId =
            localDataSource.getCartId()

        if (existingId != null) {

            return existingId
        }

        val request =
            createRequestFromItems(
                userId = userId,
                items = items
            )

        val response =
            api.addCart(
                request
            )

        localDataSource.saveCartId(
            response.id
        )

        return response.id
    }

    private fun createRequestFromItems(
        userId: Int,
        items: List<CartItem>
    ): CartRequest {

        return createRequest(
            userId = userId,

            products =
                items.map { item ->

                    CartProductRequest(
                        productId =
                            item.productId,

                        quantity =
                            item.quantity
                    )
                }
        )
    }

    private fun createRequest(
        userId: Int,
        products: List<CartProductRequest>
    ): CartRequest {

        return CartRequest(
            userId = userId,

            date =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).format(
                    Date()
                ),

            products = products
        )
    }
}