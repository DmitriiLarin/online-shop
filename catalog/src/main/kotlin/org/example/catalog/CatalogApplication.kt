package org.example.catalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["org.example.catalog.client"])
class CatalogApplication

fun main(args: Array<String>) {
    runApplication<CatalogApplication>(*args)
}
