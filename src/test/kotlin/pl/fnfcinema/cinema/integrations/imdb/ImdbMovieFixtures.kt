package pl.fnfcinema.cinema.integrations.imdb

import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

object ImdbMovieFixtures {
    fun anImdbMovie(): ImdbMovie = ImdbMovie(
        "some-title",
        LocalDate.now(),
        "110 min",
        "action",
        "unknown",
        BigDecimal("1.5"),
        ImdbInt(123),
        URI.create("http://image.com/movie.jpg"),
        "unknown"
    )
}