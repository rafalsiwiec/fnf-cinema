package pl.fnfcinema.cinema

import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.shows.ShowEntity
import pl.fnfcinema.cinema.shows.ShowId
import pl.fnfcinema.cinema.shows.ShowsApi
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
    movieId: MovieId = MovieId(UUID.randomUUID()),
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Money = aTicketPrice(),
    createdBy: StaffUserId = aStaffUserId(),
    id: ShowId? = null,
) = ShowEntity(
    movieId = movieId,
    startTime = startTime,
    ticketPrice = ticketPrice,
    createdBy = createdBy,
    id = id
)

fun aNewShow(
    movieId: UUID = UUID.randomUUID(),
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Api.Money = Api.Money(aTicketPrice()),
) = ShowsApi.Requests.NewShow(
    movieId = movieId,
    startTime = startTime,
    ticketPrice = ticketPrice
)

fun aShowUpdate(
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Api.Money = Api.Money(aTicketPrice()),
) = ShowsApi.Requests.ShowUpdate(
    startTime = startTime,
    ticketPrice = ticketPrice,
)