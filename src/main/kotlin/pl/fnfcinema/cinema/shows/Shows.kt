package pl.fnfcinema.cinema.shows

import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Error
import pl.fnfcinema.cinema.Result
import pl.fnfcinema.cinema.Success
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows.Errors.InvalidData
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class Shows(
    private val showRepository: ShowRepository,
    private val movies: Movies,
    private val clock: Clock,
) {

    fun addShow(newShow: ShowEntity): Result<ShowEntity, Errors.ShowsActionError> {
        val now = Instant.now(clock)
        if (newShow.startTime.isBefore(now)) {
            return Error(InvalidData("Show must not start in the past"))
        }

        return when (newShow.movieId.id?.let { movies.findMovie(it) }) {
            null -> Error(InvalidData("Movie with id: ${newShow.movieId.id} does not exist"))
            else -> Success(showRepository.save(newShow))
        }
    }

    fun findNearest(movieId: UUID?, limit: Int): Result<List<ShowEntity>, Errors.ShowsActionError> {
        if (limit < 1 || limit > 20) {
            return Error(InvalidData("Limit must be between 1 and 20"))
        }

        val now = Instant.now(clock)

        return Success(
            when (movieId) {
                null -> showRepository.findStartingAfter(now, limit)
                else -> showRepository.findStaringAfterByMovieId(movieId, now, limit)
            }
        )
    }

    object Errors {
        sealed interface ShowsActionError
        data class InvalidData(val details: String) : ShowsActionError
    }
}