package pl.fnfcinema.cinema.movies

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Api
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/movies")
class MoviesApi(private val movies: Movies) {

    @GetMapping
    fun getAllMovies(): List<Responses.BasicMovie> =
        movies.getAll().map { it.toBasicResponse() }

    @GetMapping("/{id}")
    fun getMovie(@PathVariable("id") id: UUID): ResponseEntity<Responses.Movie> =
        Api.entityOrNotFound(movies.getMovieDetails(id)?.toResponse(id))

    object Responses {
        data class BasicMovie(val id: UUID, val title: String)
        data class Movie(
            val id: UUID,
            val title: String,
            val releaseDate: LocalDate,
            val runtime: String,
            val genre: String,
            val director: String,
            val rating: BigDecimal,
            val ratingScale: Int,
            val votes: Int,
            val posterUrl: URI,
            val awards: String,
        )
    }

    companion object {
        private fun MovieEntity.toBasicResponse(): Responses.BasicMovie = Responses.BasicMovie(id, title)
        private fun MovieDetails.toResponse(id: UUID): Responses.Movie = Responses.Movie(
            id,
            title,
            releaseDate,
            runtime,
            genre,
            director,
            rating,
            ratingScale,
            votes,
            posterUrl,
            awards
        )
    }
}