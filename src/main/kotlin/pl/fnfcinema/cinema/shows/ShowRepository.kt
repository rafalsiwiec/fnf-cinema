package pl.fnfcinema.cinema.shows

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.Instant
import java.util.*

interface ShowRepository : CrudRepository<ShowEntity, UUID> {

    @Query("select * from show where start_time >= :after order by start_time limit :limit")
    fun findStartingAfter(after: Instant, limit: Int): List<ShowEntity>

    @Query("select * from show where movie_id = :movieId and start_time >= :after order by start_time limit :limit")
    fun findStaringAfterByMovieId(movieId: UUID, after: Instant, limit: Int): List<ShowEntity>

}