package com.example.demo.factories.auth

import com.example.demo.entities.UserEntity
import com.example.demo.models.auth.LoginRequest
import com.example.demo.models.auth.RegisterRequest

object UserTestFactory {

    val registerRequest1 = RegisterRequest(username = "testUser1", password = "testPassword1")
    val registerRequest2 = RegisterRequest(username = "testUser2", password = "testPassword2")

    val loginRequest1 = LoginRequest(username = "testUser1", password = "testPassword1")
    val loginRequest2 = LoginRequest(username = "testUser2", password = "testPassword2")

    val userEntity1 = UserEntity(id = 1L, username = "testuser", password = "hashedpassword")
}
