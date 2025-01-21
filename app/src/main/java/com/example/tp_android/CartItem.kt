package com.example.tp_android

import com.example.tp_android.Entity.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)