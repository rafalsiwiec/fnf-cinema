package pl.fnfcinema.cinema.movies

import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

interface MovieDetailsProvider<T> {

    fun fetchDetails(id: T): MovieDetails

}

data class MovieDetails(
    val title: String,
    val releaseDate: LocalDate,
    val runtime: String,
    val genre: String,
    val director: String,
    val rating: BigDecimal,
    val ratingScale: Int,
    val votes: Int,
    val posterUrl: URI,
    val awards: String
)
