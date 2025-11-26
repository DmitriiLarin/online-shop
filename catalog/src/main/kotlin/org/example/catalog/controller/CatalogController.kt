package org.example.catalog.controller

import org.example.catalog.client.AuthClient
import org.example.catalog.dao.ProductDao
import org.example.catalog.model.ProductDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/catalog")
class CatalogController(
    private val productDao: ProductDao,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/add")
    fun add(@RequestHeader(TOKEN) token: String, @RequestBody request: ProductDto): ResponseEntity<String> {
        productDao.insertProduct(request)
        return ResponseEntity.ok("Product added")
    }

    @DeleteMapping("/{id}/delete")
    fun delete(@RequestHeader(TOKEN) token: String, @PathVariable id: Long): ResponseEntity<String> {
        productDao.dsl.transaction { _ ->
            val item = productDao.fetchById(id).firstOrNull() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = authClient.getUserByToken(token)
            if (item.userId != user!!.id) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN)
            }

            productDao.deleteById(id)
        }

        return ResponseEntity.ok("Product deleted")
    }

    @PutMapping("/{id}/update")
    fun update(
        @RequestHeader(TOKEN) token: String,
        @PathVariable id: Long,
        @RequestBody request: ProductDto
    ): ResponseEntity<String> {
        productDao.dsl.transaction { _ ->
            val item = productDao.fetchById(id).firstOrNull() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = authClient.getUserByToken(token)
            if (item.userId != user!!.id) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN)
            }

            productDao.updateById(id, request)
        }

        return ResponseEntity.ok("Product updated")
    }
}