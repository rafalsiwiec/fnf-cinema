package pl.fnfcinema.cinema.movies

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.asErrorResponse
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.Movies.Errors.MoviesError
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
        Api.Security.requireStaffUserId()
        val movie = movies.addMovie(newMovie.toMovieEntity()).toBasicResponse()
        return ResponseEntity.status(201).body(movie)
    }

    @GetMapping("/{id}")
    fun getMovie(@PathVariable("id") id: UUID): ResponseEntity<Responses.Movie> =
        Api.entityOrNotFound(movies.getMovieDetails(id)?.toResponse(id))

    @PostMapping("/{id}/rating/{rate}")
    fun rate(
        @PathVariable("id") id: UUID,
        @PathVariable("rate") rate: Int,
    ): ResponseEntity<BasicMovie> =
        when (val result = movies.rate(id, rate)) {
            is Succ<MovieEntity> -> ResponseEntity.ok(result.value.toBasicResponse())
            is Err<MoviesError> -> result.asErrorResponse(::errorDetails)
        }

    object Requests {
        data class NewMovie(val title: String, val imdbId: String)
    }

    object Responses {
        data class BasicMovie(val id: UUID, val title: String, val votes: Long = 0, val avgRate: BigDecimal? = null)
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
        private fun MovieEntity.toBasicResponse(): BasicMovie = BasicMovie(id!!, title, rating.votes, rating.avg())
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

        private fun errorDetails(moviesError: MoviesError): Pair<Int, String> =
            when (moviesError) {
                is Movies.Errors.BadInput -> 400 to moviesError.details
                is Movies.Errors.MovieNotFound -> 404 to "Movie with id: ${moviesError.id} not found"
            }
    }
}