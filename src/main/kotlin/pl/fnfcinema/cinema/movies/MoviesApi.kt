package pl.fnfcinema.cinema.movies

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.movies.MoviesApi.Requests.NewMovie
import pl.fnfcinema.cinema.movies.MoviesApi.Responses.BasicMovie
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/movies")
class MoviesApi(private val movies: Movies) {

    @GetMapping
    fun getAllMovies(): List<BasicMovie> =
        movies.getAll().map { it.toBasicResponse() }

    @PostMapping
    fun addMovie(@RequestBody newMovie: NewMovie): ResponseEntity<BasicMovie> {
        val movie = movies.addMovie(newMovie.toMovieEntity()).toBasicResponse()
        return ResponseEntity.status(201).body(movie)
    }

    @GetMapping("/{id}")
    fun getMovie(@PathVariable("id") id: UUID): ResponseEntity<Responses.Movie> =
        Api.entityOrNotFound(movies.getMovieDetails(id)?.toResponse(id))

    object Requests {
        data class NewMovie(val title: String, val imdbId: String)
    }

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
        private fun MovieEntity.toBasicResponse(): BasicMovie = BasicMovie(id!!, title)
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
        private fun NewMovie.toMovieEntity(): MovieEntity = MovieEntity(title, imdbId)
    }
}