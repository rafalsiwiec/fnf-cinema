package pl.fnfcinema.cinema.movies

import org.mockito.Mockito.mock
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import pl.fnfcinema.cinema.integrations.imdb.ImdbApi

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTest.Config::class)
abstract class IntegrationTest {

    companion object {
        val imdbApi: ImdbApi = mock()
    }

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun imdbApi(): ImdbApi = imdbApi
    }
}