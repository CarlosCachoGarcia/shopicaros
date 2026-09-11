package com.example.shopicaros.data.local

import android.content.Context
import com.example.shopicaros.data.model.CartItem
import org.json.JSONArray
import org.json.JSONObject

class SharedPreferencesCartLocalDataSource(
    context: Context
) : CartLocalDataSource {

    private val preferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    override fun getItems(): List<CartItem> {

        val json =
            preferences.getString(
                KEY_CART,
                null
            ) ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(json)

            val items =
                mutableListOf<CartItem>()

            for (
            index in 0 until jsonArray.length()
            ) {

                val jsonObject =
                    jsonArray.getJSONObject(index)

                items.add(
                    CartItem(
                        productId =
                            jsonObject.getInt(
                                KEY_PRODUCT_ID
                            ),

                        title =
                            jsonObject.getString(
                                KEY_TITLE
                            ),

                        price =
                            jsonObject.getDouble(
                                KEY_PRICE
                            ),

                        image =
                            jsonObject.getString(
                                KEY_IMAGE
                            ),

                        quantity =
                            jsonObject.getInt(
                                KEY_QUANTITY
                            )
                    )
                )
            }

            items

        } catch (e: Exception) {

            emptyList()
        }
    }

    override fun addOrUpdate(
        item: CartItem
    ): CartItem {

        val currentItems =
            getItems()
                .toMutableList()

        val existingIndex =
            currentItems.indexOfFirst {
                it.productId == item.productId
            }

        val finalItem =
            if (existingIndex >= 0) {

                val existingItem =
                    currentItems[
                        existingIndex
                    ]

                val updatedItem =
                    existingItem.copy(
                        quantity =
                            existingItem.quantity +
                                    item.quantity
                    )

                currentItems[
                    existingIndex
                ] = updatedItem

                updatedItem

            } else {

                currentItems.add(
                    item
                )

                item
            }

        saveItems(
            currentItems
        )

        return finalItem
    }

    override fun updateQuantity(
        productId: Int,
        quantity: Int
    ): List<CartItem> {

        val currentItems =
            getItems()
                .toMutableList()

        val index =
            currentItems.indexOfFirst {
                it.productId == productId
            }

        if (index < 0) {
            return currentItems
        }

        if (quantity <= 0) {

            currentItems.removeAt(
                index
            )

        } else {

            val currentItem =
                currentItems[index]

            currentItems[index] =
                currentItem.copy(
                    quantity = quantity
                )
        }

        saveItems(
            currentItems
        )

        return currentItems
    }

    override fun removeItem(
        productId: Int
    ): List<CartItem> {

        val currentItems =
            getItems()
                .filterNot {
                    it.productId ==
                            productId
                }

        saveItems(
            currentItems
        )

        return currentItems
    }

    override fun saveCartId(
        cartId: Int
    ) {

        preferences
            .edit()
            .putInt(
                KEY_CART_ID,
                cartId
            )
            .apply()
    }

    override fun getCartId(): Int? {

        val cartId =
            preferences.getInt(
                KEY_CART_ID,
                -1
            )

        return if (
            cartId > 0
        ) {
            cartId
        } else {
            null
        }
    }

    override fun clear() {

        preferences
            .edit()
            .clear()
            .apply()
    }

    private fun saveItems(
        items: List<CartItem>
    ) {

        val jsonArray =
            JSONArray()

        items.forEach { item ->

            val jsonObject =
                JSONObject().apply {

                    put(
                        KEY_PRODUCT_ID,
                        item.productId
                    )

                    put(
                        KEY_TITLE,
                        item.title
                    )

                    put(
                        KEY_PRICE,
                        item.price
                    )

                    put(
                        KEY_IMAGE,
                        item.image
                    )

                    put(
                        KEY_QUANTITY,
                        item.quantity
                    )
                }

            jsonArray.put(
                jsonObject
            )
        }

        preferences
            .edit()
            .putString(
                KEY_CART,
                jsonArray.toString()
            )
            .apply()
    }

    companion object {

        private const val PREFS_NAME =
            "cart_session"

        private const val KEY_CART =
            "cart_items"

        private const val KEY_CART_ID =
            "cart_id"

        private const val KEY_PRODUCT_ID =
            "product_id"

        private const val KEY_TITLE =
            "title"

        private const val KEY_PRICE =
            "price"

        private const val KEY_IMAGE =
            "image"

        private const val KEY_QUANTITY =
            "quantity"
    }
}