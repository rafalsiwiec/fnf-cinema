package pl.fnfcinema.cinema.shows

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Res
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowsError
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class Shows(
    private val showRepository: ShowRepository,
    private val movies: Movies,
    private val clock: Clock,
) {

    fun addShow(newShow: ShowEntity): Res<ShowEntity, ShowsError> {
        val now = Instant.now(clock)
        if (newShow.startTime.isBefore(now)) {
            return Err(Errors.BadInput("Show must not start in the past"))
        }

        return when (newShow.movieId.id?.let { movies.findMovie(it) }) {
            null -> Err(Errors.BadInput("Movie with id: ${newShow.movieId.id} does not exist"))
            else -> Succ(showRepository.save(newShow))
        }
    }

    fun deleteShow(showId: UUID): Res<Unit, ShowsError> =
        showRepository.findByIdOrNull(showId)?.let {
            showRepository.delete(it)
            Succ(Unit)
        } ?: Err(Errors.ShowNotFound(showId))

    fun updateShow(showId: UUID, startTime: Instant, ticketPrice: Money): Res<ShowEntity, ShowsError> =
        showRepository.findByIdOrNull(showId)?.let {
            Succ(showRepository.save(it.copy(startTime = startTime, ticketPrice = ticketPrice)))
        } ?: Err(Errors.ShowNotFound(showId))

    fun findNearest(movieId: UUID?, limit: Int): Res<List<ShowEntity>, ShowsError> {
        if (limit < 1 || limit > 20) {
            return Err(Errors.BadInput("Limit must be between 1 and 20"))
        }

        val now = Instant.now(clock)

        return Succ(
            when (movieId) {
                null -> showRepository.findStartingAfter(now, limit)
                else -> showRepository.findStaringAfterByMovieId(movieId, now, limit)
            }
        )
    }

    object Errors {
        sealed interface ShowsError
        data class BadInput(val details: String) : ShowsError
        data class ShowNotFound(val id: UUID) : ShowsError
    }
}