package pl.fnfcinema.cinema.movies

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.ALL
import org.springframework.http.MediaType.ALL_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.Security.STAFF_ONLY
import pl.fnfcinema.cinema.Api.asErrorResponse
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.Movies.Errors.MoviesError
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.util.*


@Tag(
    name = "Movies API",
    description = "Manages movies"
)
@RestController
@RequestMapping("/movies")
class MoviesApi(private val movies: Movies) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getAllMovies(): List<BasicMovieRes> = movies.getAll().map { it.toBasicResponse() }

    @SecurityRequirement(name = STAFF_ONLY)
    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun addMovie(@RequestBody addMovieReq: AddMovieReq): ResponseEntity<BasicMovieRes> {
        Api.Security.requireStaffUserId()
        val movie = movies.addMovie(addMovieReq.toMovieEntity()).toBasicResponse()
        return ResponseEntity.status(201).body(movie)
    }

    @GetMapping("/{id}", produces = [APPLICATION_JSON_VALUE])
    fun getMovie(@PathVariable("id") id: UUID): ResponseEntity<MovieDetailsRes> =
        Api.entityOrNotFound(movies.getMovieDetails(MovieId(id))?.toResponse(id))

    @PostMapping("/{id}/rating/{rate}", consumes = [ALL_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun rate(
        @PathVariable("id") id: UUID,
        @PathVariable("rate") rate: Int,
    ): ResponseEntity<BasicMovieRes> = when (val result = movies.rate(MovieId(id), rate)) {
        is Succ<MovieEntity> -> ResponseEntity.ok(result.value.toBasicResponse())
        is Err<MoviesError> -> result.asErrorResponse(::errorDetails)
    }

    data class AddMovieReq(val title: String, val imdbId: String)
    data class BasicMovieRes(val id: UUID, val title: String, val votes: Long = 0, val avgRate: BigDecimal? = null)
    data class MovieDetailsRes(
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

    companion object {
        private fun MovieEntity.toBasicResponse(): BasicMovieRes =
            BasicMovieRes(id!!.value, title, rating.votes, rating.avg())

        private fun MovieDetails.toResponse(id: UUID): MovieDetailsRes = MovieDetailsRes(
            id, title, releaseDate, runtime, genre, director, rating, ratingScale, votes, posterUrl, awards
        )

        private fun AddMovieReq.toMovieEntity(): MovieEntity = MovieEntity(title, imdbId)

        private fun errorDetails(moviesError: MoviesError): Pair<Int, String> = when (moviesError) {
            is Movies.Errors.BadInput -> 400 to moviesError.details
            is Movies.Errors.MovieNotFound -> 404 to "Movie with id: ${moviesError.id} not found"
        }
    }

}