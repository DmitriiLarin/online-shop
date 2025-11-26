package org.example.catalog.model

data class ProductDto(
    val price : Int,
    val quantity : Int,
    val image : String,
    val category: String,
    val userId: Long
)
