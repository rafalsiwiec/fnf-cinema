package pl.fnfcinema.cinema

import pl.fnfcinema.cinema.integrations.imdb.ImdbLong
import pl.fnfcinema.cinema.integrations.imdb.ImdbMovie
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

fun anImdbId(): String = faker.string.numerify("tt#######")
fun anImdbMovie() = ImdbMovie(
    faker.movie.title(),
    LocalDate.now(),
    "110 min",
    "action",
    "unknown",
    BigDecimal("1.5"),
    ImdbLong(123),
    URI.create("http://image.com/movie.jpg"),
    "unknown"
)