package pl.fnfcinema.cinema.shows

import org.springframework.data.jdbc.core.mapping.AggregateReference
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.movies.MovieEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object ShowEntityFixtures {

    fun aShow(
        movie: MovieEntity,
        startTime: Instant = Instant.now().plus(1.days.toJavaDuration()),
    ) = aShow(movie.id!!, startTime)

    fun aShow(
        movieId: UUID = UUID.randomUUID(),
        startTime: Instant = Instant.now().plus(1.days.toJavaDuration()),
        money: Money = Money(BigDecimal("35.00"), Currency.getInstance("PLN")),
    ) = ShowEntity(AggregateReference.to(movieId), startTime, money)
}