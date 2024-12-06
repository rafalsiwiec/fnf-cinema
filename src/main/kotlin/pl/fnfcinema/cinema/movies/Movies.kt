package pl.fnfcinema.cinema.movies

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.OptimisticLocking.rerunOnConflict
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

    fun rate(id: UUID, rate: Int): MovieEntity? = rerunOnConflict(withinMax = 500.milliseconds) {
        movieRepository.findByIdOrNull(id)?.let { movieRepository.save(it.rate(rate)) }
    }
}
