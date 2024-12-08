package pl.fnfcinema.cinema.shows

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY
import org.springframework.data.relational.core.mapping.Table
import pl.fnfcinema.cinema.EntityId
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.StaffUserId
import pl.fnfcinema.cinema.movies.MovieId
import java.time.Instant
import java.util.*

@Table("show")
data class ShowEntity(
    val movieId: MovieId,
    val startTime: Instant,
    @Embedded(onEmpty = USE_EMPTY, prefix = "ticket_price_")
    val ticketPrice: Money,
    val createdBy: StaffUserId,
    @Version val version: Int = 0,
    @Id val id: ShowId? = null,
)

data class ShowDetails(
    val movieId: MovieId,
    val movieTitle: String,
    val startTime: Instant,
    @Embedded(onEmpty = USE_EMPTY, prefix = "ticket_price_")
    val ticketPrice: Money,
    val id: ShowId,
)

@JvmInline
value class ShowId(override val value: UUID) : EntityId
