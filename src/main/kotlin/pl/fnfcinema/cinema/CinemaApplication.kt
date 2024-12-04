package pl.fnfcinema.cinema

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import pl.fnfcinema.cinema.integrations.imdb.ImdbIntegrationProperties
import pl.fnfcinema.cinema.integrations.imdb.ImdbMoviesCacheProperties


@SpringBootApplication
@EnableConfigurationProperties(
    ImdbIntegrationProperties::class,
    ImdbMoviesCacheProperties::class
)
class CinemaApplication

fun main(args: Array<String>) {
    runApplication<CinemaApplication>(*args)
}
