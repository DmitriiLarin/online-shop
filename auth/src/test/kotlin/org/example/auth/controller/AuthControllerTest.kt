package org.example.auth.controller

import org.example.auth.dao.AuthDao
import org.example.auth.dto.request.RegisterRequest
import org.example.auth.jooq.tables.pojos.Users
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime

@SpringBootTest
class AuthControllerTest {

    @MockitoBean
    lateinit var authDao: AuthDao

    @Autowired
    lateinit var authController: AuthController

    @Test
    fun contextLoads() {
        val user = Users(2, "1", "1", "1", LocalDateTime.now(), LocalDateTime.now())

        authDao.insert(user)
        authController.register(RegisterRequest("1", "1", "1"))
    }
}