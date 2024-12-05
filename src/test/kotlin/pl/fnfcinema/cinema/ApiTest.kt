package pl.fnfcinema.cinema

import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import pl.fnfcinema.cinema.movies.Movies

@SpringBootTest
@AutoConfigureMockMvc
@Import(ApiTest.Config::class)
abstract class ApiTest {

    @BeforeEach
    fun setUp() {
        Mockito.reset(movies)
    }

    companion object {
        val movies: Movies = mock()
    }

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun movies(): Movies = movies
    }
}