package pl.fnfcinema.cinema

import io.github.serpro69.kfaker.Faker
import org.springframework.data.jdbc.core.mapping.AggregateReference
import pl.fnfcinema.cinema.integrations.imdb.ImdbInt
import pl.fnfcinema.cinema.integrations.imdb.ImdbMovie
import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.movies.Rate
import pl.fnfcinema.cinema.movies.Rating
import pl.fnfcinema.cinema.shows.ShowEntity
import java.math.BigDecimal
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

private val faker = Faker()

fun aTicketPrice(): Money =
    Money(faker.random.nextInt(20, 45).toBigDecimal().setScale(2))

fun anImdbId(): String = faker.string.numerify("tt#######")

fun aMovie(
    rating: Rating = Rating(),
    id: UUID? = null,
) =
    MovieEntity(
        title = faker.movie.title(),
        imdbId = anImdbId(),
        rating = rating,
        id = id,
    )

fun aRate(): Rate =
    Rate(faker.random.nextInt(1, 5))

fun aShow(
    movie: MovieEntity,
    startTime: Instant = Instant.now() + 1.days.toJavaDuration(),
) = aShow(movie.id!!, startTime)

fun aShow(
    movieId: UUID = UUID.randomUUID(),
    startTime: Instant = Instant.now()
        .plus(1.days.toJavaDuration()),
    money: Money = Money(
        BigDecimal("35.00"),
        Currency.PLN
    ),
    id: UUID? = null,
) = ShowEntity(
    movieId = AggregateReference.to(movieId),
    startTime = startTime,
    ticketPrice = money,
    id = id
)

fun anImdbMovie(): ImdbMovie =
    ImdbMovie(
        faker.movie.title(),
        LocalDate.now(),
        "110 min",
        "action",
        "unknown",
        BigDecimal("1.5"),
        ImdbInt(123),
        URI.create("http://image.com/movie.jpg"),
        "unknown"
    )

fun <R, E> Res<R, E>.requireSucc(): R = when (this) {
    is Succ<R> -> value
    is Err<E> -> throw IllegalStateException()
}