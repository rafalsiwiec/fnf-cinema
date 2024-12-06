package pl.fnfcinema.cinema.movies

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.OptimisticLocking.rerunOnConflict
import pl.fnfcinema.cinema.Res
import pl.fnfcinema.cinema.Succ
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Service
class Movies(
    private val movieRepository: MovieRepository,
    private val movieDetailsProvider: MovieDetailsProvider,
) {

    fun getAll(): List<MovieEntity> = movieRepository.findAll().toList()

    fun getMovieDetails(id: UUID): MovieDetails? =
        movieRepository.findByIdOrNull(id)?.let {
            movieDetailsProvider.fetchDetails(it.imdbId)
        }

    fun addMovie(newMovie: MovieEntity): MovieEntity = movieRepository.save(newMovie)

    fun findMovie(id: UUID): MovieEntity? = movieRepository.findByIdOrNull(id)

    fun rate(id: UUID, rateValue: Int): Res<MovieEntity, Errors.MoviesError> {
        val rate = Rate.create(rateValue) ?: return Err(Errors.BadInput("Invalid rate value"))

        return rerunOnConflict(withinMax = 500.milliseconds) {
            when (val movie = movieRepository.findByIdOrNull(id)) {
                null -> Err(Errors.MovieNotFound(id))
                else -> Succ(movieRepository.save(movie.rate(rate)))
            }
        }
    }

    object Errors {
        sealed interface MoviesError
        data class BadInput(val details: String) : MoviesError
        data class MovieNotFound(val id: UUID) : MoviesError
    }
}
