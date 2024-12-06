package pl.fnfcinema.cinema.shows

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY
import org.springframework.data.relational.core.mapping.Table
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.movies.MovieEntity
import java.time.Instant
import java.util.*

@Table("show")
data class ShowEntity(
    val movieId: AggregateReference<MovieEntity, UUID>,
    val startTime: Instant,
    @Embedded(onEmpty = USE_EMPTY)
    val ticketPrice: Money,
    @Version val version: Int = 0,
    @Id val id: UUID? = null,
)
