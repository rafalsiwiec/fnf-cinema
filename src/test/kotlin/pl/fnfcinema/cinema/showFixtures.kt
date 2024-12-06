package pl.fnfcinema.cinema

import org.springframework.data.jdbc.core.mapping.AggregateReference
import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.shows.ShowEntity
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

fun aTicketPrice(): Money = Money(faker.random.nextInt(20, 45).toBigDecimal().setScale(2))
fun aFutureStartTime(): Instant = Instant.now() + 1.days.toJavaDuration()
fun aShow(
    movie: MovieEntity,
    startTime: Instant = aFutureStartTime(),
) = aShow(movieId = movie.id!!, startTime = startTime)

fun aShow(
    movieId: UUID = UUID.randomUUID(),
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Money = aTicketPrice(),
    id: UUID? = null,
) = ShowEntity(
    movieId = AggregateReference.to(movieId),
    startTime = startTime,
    ticketPrice = ticketPrice,
    id = id
)