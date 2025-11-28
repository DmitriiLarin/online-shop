package org.example.catalog.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.catalog.client.AuthClient
import org.example.catalog.dao.ProductDao
import org.example.catalog.jooq.tables.pojos.Product
import org.example.catalog.model.ProductDto
import org.example.catalog.dto.response.UserDataResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class CatalogControllerTest {

    private lateinit var productDao: ProductDao
    private lateinit var authClient: AuthClient
    private lateinit var catalogController: CatalogController

    @BeforeEach
    fun setUp() {
        productDao = mockk()
        authClient = mockk()
        catalogController = CatalogController(productDao, authClient)
    }

    @Test
    fun `add should return success when product is added`() {
        val token = "Bearer test-token"
        val productDto = ProductDto(
            price = 100,
            quantity = 10,
            image = "image.jpg",
            category = "Electronics",
            userId = 1L
        )

        every { productDao.insertProduct(productDto) } returns Unit

        val response = catalogController.add(token, productDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Product added", response.body)
        verify { productDao.insertProduct(productDto) }
    }

    @Test
    fun `delete should return success when product is deleted`() {
        val token = "Bearer test-token"
        val productId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val product = Product(
            productId,
            100,
            5,
            "image.jpg",
            "Electronics",
            user.id,
        )

        every { productDao.fetchById(productId) } returns listOf(product)
        every { authClient.getUserByToken(token) } returns user
        every { productDao.deleteById(productId) } returns Unit

        val response = catalogController.delete(token, productId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Product deleted", response.body)
        verify { productDao.fetchById(productId) }
        verify { authClient.getUserByToken(token) }
        verify { productDao.deleteById(productId) }
    }

    @Test
    fun `update should return success when product is updated`() {
        val token = "Bearer test-token"
        val productId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val product = Product(
            productId,
            100,
            5,
            "image.jpg",
            "Electronics",
            user.id,
        )
        val productDto = ProductDto(
            price = 150,
            quantity = 15,
            image = "newimage.jpg",
            category = "Electronics",
            userId = user.id
        )

        every { productDao.fetchById(productId) } returns listOf(product)
        every { authClient.getUserByToken(token) } returns user
        every { productDao.updateById(productId, productDto) } returns Unit

        val response = catalogController.update(token, productId, productDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Product updated", response.body)
        verify { productDao.fetchById(productId) }
        verify { authClient.getUserByToken(token) }
        verify { productDao.updateById(productId, productDto) }
    }
}

