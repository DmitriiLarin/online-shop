package org.example.auth.dao

import org.example.auth.model.User
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AuthDao(
    private val dsl: DSLContext
) {
    private val USERS = DSL.table("users")
    private val ID = DSL.field("id", Long::class.java)
    private val USERNAME = DSL.field("username", String::class.java)
    private val PASSWORD = DSL.field("password", String::class.java)
    private val EMAIL = DSL.field("email", String::class.java)
    private val CREATED_AT = DSL.field("created_at", LocalDateTime::class.java)
    private val UPDATED_AT = DSL.field("updated_at", LocalDateTime::class.java)

    fun findByUsername(username: String): User? {
        val record = dsl.select()
            .from(USERS)
            .where(USERNAME.eq(username))
            .fetchOne()

        return record?.let { mapToUser(it) }
    }

    fun findByEmail(email: String): User? {
        val record = dsl.select()
            .from(USERS)
            .where(EMAIL.eq(email))
            .fetchOne()

        return record?.let { mapToUser(it) }
    }

    fun findById(id: Long): User? {
        val record = dsl.select()
            .from(USERS)
            .where(ID.eq(id))
            .fetchOne()

        return record?.let { mapToUser(it) }
    }

    fun createUser(user: User): User {
        val now = LocalDateTime.now()
        val id = dsl.insertInto(USERS)
            .set(USERNAME, user.username)
            .set(PASSWORD, user.password)
            .set(EMAIL, user.email)
            .set(CREATED_AT, now)
            .set(UPDATED_AT, now)
            .returningResult(ID)
            .fetchOne()
            ?.getValue(ID)

        return user.copy(id = id, createdAt = now, updatedAt = now)
    }

    fun updatePassword(userId: Long, newPassword: String) {
        dsl.update(USERS)
            .set(PASSWORD, newPassword)
            .set(UPDATED_AT, LocalDateTime.now())
            .where(ID.eq(userId))
            .execute()
    }

    fun updateEmail(userId: Long, newEmail: String) {
        dsl.update(USERS)
            .set(EMAIL, newEmail)
            .set(UPDATED_AT, LocalDateTime.now())
            .where(ID.eq(userId))
            .execute()
    }

    fun updateUser(userId: Long, email: String?) {
        val update = dsl.update(USERS)
            .set(UPDATED_AT, LocalDateTime.now())
        
        if (email != null) {
            update.set(EMAIL, email)
        }
        
        update.where(ID.eq(userId))
            .execute()
    }

    fun deleteUser(userId: Long) {
        dsl.deleteFrom(USERS)
            .where(ID.eq(userId))
            .execute()
    }

    private fun mapToUser(record: Record): User {
        return User(
            id = record.get(ID),
            username = record.get(USERNAME)!!,
            password = record.get(PASSWORD)!!,
            email = record.get(EMAIL)!!,
            createdAt = record.get(CREATED_AT),
            updatedAt = record.get(UPDATED_AT)
        )
    }
}