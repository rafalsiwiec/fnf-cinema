package pl.fnfcinema.cinema.shows

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import pl.fnfcinema.cinema.movies.MovieId
import java.time.Instant

interface ShowRepository : CrudRepository<ShowEntity, ShowId>

interface ShowQueryRepository : PagingAndSortingRepository<ShowDetails, ShowId> {

    fun findByStartTimeAfter(afterStartTime: Instant, pageable: Pageable): List<ShowDetails>
    fun findByMovieIdAndStartTimeAfter(movieId: MovieId, afterStartTime: Instant, pageable: Pageable): List<ShowDetails>

}