package org.example.auth.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class AuthDao(
    private val dsl: DSLContext
) {

    fun getUser() {
        dsl.selectFrom()
    }
}