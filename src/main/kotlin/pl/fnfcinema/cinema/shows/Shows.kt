package pl.fnfcinema.cinema.shows

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Res
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowsError
import java.time.Clock
import java.time.Instant

@Service
class Shows(
    private val repo: ShowRepository,
    private val movies: Movies,
    private val clock: Clock,
) {

    fun addShow(newShow: ShowEntity): Res<ShowEntity, ShowsError> {
        val now = Instant.now(clock)
        if (newShow.startTime.isBefore(now)) return Err(Errors.BadInput("Show must not start in the past"))

        movies.findMovie(newShow.movieId)
            ?: return Err(Errors.BadInput("Movie with id: ${newShow.movieId.value} does not exist"))

        return Succ(repo.save(newShow))
    }

    fun deleteShow(showId: ShowId): Res<Unit, ShowsError> {
        val show = repo.findByIdOrNull(showId) ?: return Err(Errors.ShowNotFound(showId))
        repo.delete(show)
        return Succ(Unit)
    }

    fun updateShow(showId: ShowId, startTime: Instant, ticketPrice: Money): Res<ShowEntity, ShowsError> {
        val show = repo.findByIdOrNull(showId) ?: return Err(Errors.ShowNotFound(showId))
        return Succ(repo.save(show.copy(startTime = startTime, ticketPrice = ticketPrice)))
    }

    fun findNearest(movieId: MovieId?, limit: Int): Res<List<ShowEntity>, ShowsError> {
        if (limit < 1 || limit > 20) return Err(Errors.BadInput("Limit must be between 1 and 20"))

        val now = Instant.now(clock)

        return Succ(
            when (movieId) {
                null -> repo.findStartingAfter(now, limit)
                else -> repo.findStaringAfterByMovieId(movieId, now, limit)
            }
        )
    }

    object Errors {
        sealed interface ShowsError
        data class BadInput(val details: String) : ShowsError
        data class ShowNotFound(val id: ShowId) : ShowsError
    }
}