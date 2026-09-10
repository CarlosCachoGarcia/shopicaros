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
            CartRequest(
                userId = userId,

                date =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                    ).format(
                        Date()
                    ),

                products =
                    listOf(
                        CartProductRequest(
                            productId =
                                product.id,

                            quantity =
                                quantity
                        )
                    )
            )

        // Fake Store API simula el registro.
        api.addCart(
            request
        )

        // Después de que la petición fue exitosa,
        // conservamos el artículo localmente.
        val localItem =
            CartItem(
                productId =
                    product.id,

                title =
                    product.title,

                price =
                    product.price,

                image =
                    product.image,

                quantity =
                    quantity
            )

        return localDataSource.addOrUpdate(
            localItem
        )
    }

    override fun getLocalItems(): List<CartItem> {

        return localDataSource.getItems()
    }

    override fun clearLocalCart() {

        localDataSource.clear()
    }
}