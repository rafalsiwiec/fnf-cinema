package pl.fnfcinema.cinema.movies

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.OptimisticLocking.rerunOnConflict
import pl.fnfcinema.cinema.Res
import pl.fnfcinema.cinema.Succ
import kotlin.time.Duration.Companion.milliseconds

@Service
class Movies(
    private val repo: MovieRepository,
    private val movieDetailsProvider: MovieDetailsProvider,
) {

    fun getAll(): List<MovieEntity> = repo.findAll().toList()

    fun getMovieDetails(id: MovieId): MovieDetails? =
        repo.findByIdOrNull(id)?.let {
            movieDetailsProvider.fetchDetails(it.imdbId)
        }

    fun addMovie(newMovie: MovieEntity): MovieEntity = repo.save(newMovie)

    fun findMovie(id: MovieId): MovieEntity? = repo.findByIdOrNull(id)

    fun rate(id: MovieId, rateValue: Int): Res<MovieEntity, Errors.MoviesError> {
        val rate = Rate.fromInt(rateValue) ?: return Err(Errors.BadInput("Invalid rate value"))

        return rerunOnConflict(withinAtMax = 500.milliseconds) {
            val movie = repo.findByIdOrNull(id) ?: return@rerunOnConflict Err(Errors.MovieNotFound(id))
            Succ(repo.save(movie.rate(rate)))
        }
    }

    object Errors {
        sealed interface MoviesError
        data class BadInput(val details: String) : MoviesError
        data class MovieNotFound(val id: MovieId) : MoviesError
    }
}
