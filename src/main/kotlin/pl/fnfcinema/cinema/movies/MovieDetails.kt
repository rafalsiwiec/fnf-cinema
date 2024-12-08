package pl.fnfcinema.cinema.movies

import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

data class MovieDetails(
    val releaseDate: LocalDate,
    val runtime: String,
    val genre: String,
    val director: String,
    val rating: BigDecimal,
    val ratingScale: Int,
    val votes: Long,
    val posterUrl: URI,
    val awards: String
)