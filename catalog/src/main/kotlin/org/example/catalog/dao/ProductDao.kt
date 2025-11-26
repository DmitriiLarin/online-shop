package org.example.catalog.dao

import org.example.auth.jooq.tables.Product.PRODUCT
import org.example.auth.jooq.tables.daos.ProductDao
import org.example.catalog.model.ProductDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ProductDao(
     val dsl: DSLContext
) : ProductDao(dsl.configuration()) {

    fun insertProduct(product: ProductDto) {
        dsl.insertInto(PRODUCT)
            .set(PRODUCT.PRICE, product.price)
            .set(PRODUCT.QUANTITY, product.quantity)
            .set(PRODUCT.IMAGE, product.image)
            .set(PRODUCT.CATEGORY, product.category)
            .set(PRODUCT.USER_ID, product.userId)
    }

    fun updateById(id: Long, product: ProductDto) {
        dsl.update(PRODUCT)
        .set(PRODUCT.PRICE, product.price)
        .set(PRODUCT.QUANTITY, product.quantity)
        .set(PRODUCT.IMAGE, product.image)
        .set(PRODUCT.CATEGORY, product.category)
        .where(PRODUCT.ID.eq(id))
    }
}