package pl.fnfcinema.cinema

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer

@Configuration
class DatabaseConfig {

    @Bean
    @ServiceConnection
    fun database(): PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17-alpine")

}