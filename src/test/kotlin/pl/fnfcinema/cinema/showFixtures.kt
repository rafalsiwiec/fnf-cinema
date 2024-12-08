package pl.fnfcinema.cinema

import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.shows.ShowDetails
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

fun aShowDetails(
    movieId: MovieId = MovieId(UUID.randomUUID()),
    movieTitle: String = aMovieTitle(),
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Money = aTicketPrice(),
    id: ShowId = ShowId(UUID.randomUUID()),
) = ShowDetails(
    movieId = movieId,
    movieTitle = movieTitle,
    startTime = startTime,
    ticketPrice = ticketPrice,
    id = id
)

fun anAddShowReq(
    movieId: UUID = UUID.randomUUID(),
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Api.Money = Api.Money(aTicketPrice()),
) = ShowsApi.AddShowReq(
    movieId = movieId,
    startTime = startTime,
    ticketPrice = ticketPrice
)

fun anUpdateShowReq(
    startTime: Instant = aFutureStartTime(),
    ticketPrice: Api.Money = Api.Money(aTicketPrice()),
) = ShowsApi.UpdateShowReq(
    startTime = startTime,
    ticketPrice = ticketPrice,
)