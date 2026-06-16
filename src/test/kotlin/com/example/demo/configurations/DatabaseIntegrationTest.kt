package com.example.demo.configurations

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Sql(scripts = ["/reset.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
annotation class DatabaseIntegrationTest
