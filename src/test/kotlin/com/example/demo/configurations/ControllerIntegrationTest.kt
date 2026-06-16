package com.example.demo.configurations

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = ["/reset.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
annotation class ControllerIntegrationTest
