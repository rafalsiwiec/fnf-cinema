package pl.fnfcinema.cinema.shows

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.IntegrationTest
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows.Errors.BadInput
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ShowsTest(
    @Autowired val shows: Shows,
    @Autowired val movies: Movies,
) : IntegrationTest() {

    @Test
    fun should_return_nearest_shows_for_limit_larger_than_actual_shows() {
        // given
        val time = mutableClock.stopTime()
        val fixtures = Fixtures(movies, shows, time)

        // when
        val nearestAtMax10Shows = shows.findNearest(null, 10)

        // then
        assertEquals(
            Succ(
                listOf(
                    fixtures.movieATomorrowShow,
                    fixtures.movieBDayAfterTomorrowShow,
                    fixtures.movieANextWeekShow,
                    fixtures.movieBNextWeekShow
                )
            ),
            nearestAtMax10Shows
        )
    }

    @Test
    fun should_return_nearest_shows_for_limit_smaller_than_actual_shows() {
        // given
        val time = mutableClock.stopTime()
        val fixtures = Fixtures(movies, shows, time)

        // when
        val nearestAtMax3Shows = shows.findNearest(null, 3)

        // then
        assertEquals(
            Succ(
                listOf(
                    fixtures.movieATomorrowShow,
                    fixtures.movieBDayAfterTomorrowShow,
                    fixtures.movieANextWeekShow
                )
            ),
            nearestAtMax3Shows
        )
    }

    @Test
    fun should_filter_out_past_shows() {
        // given
        val time = mutableClock.stopTime()
        val fixtures = Fixtures(movies, shows, time)

        // when
        val nearestAtMax10MovieAShows = shows.findNearest(fixtures.movieA.id, 10)

        // then
        assertEquals(
            Succ(
                listOf(
                    fixtures.movieATomorrowShow,
                    fixtures.movieANextWeekShow,
                )
            ),
            nearestAtMax10MovieAShows
        )

        // given - time is 26h later
        mutableClock.addTime(26.hours)

        // when
        val nearestAtMax10ShowsFetched26hLater = shows.findNearest(null, 10)

        // then
        assertEquals(
            Succ(
                listOf(
                    fixtures.movieBDayAfterTomorrowShow,
                    fixtures.movieANextWeekShow,
                    fixtures.movieBNextWeekShow
                )
            ),
            nearestAtMax10ShowsFetched26hLater
        )
    }

    @Test
    fun should_validate_if_movie_exists() {
        // given
        val time = mutableClock.stopTime()
        val unknownMovieId = UUID.randomUUID()

        // when
        val result = shows.addShow(aShow(movieId = unknownMovieId, time + 1.days.toJavaDuration()))

        // then
        assertEquals(
            Err(BadInput("Movie with id: $unknownMovieId does not exist")),
            result
        )
    }

    @Test
    fun should_validate_if_show_start_time_is_not_in_the_past() {
        // given
        val time = mutableClock.stopTime()
        val movie = movies.addMovie(aMovie())

        // when
        val result = shows.addShow(aShow(movie = movie, time - 1.seconds.toJavaDuration()))

        // then
        assertEquals(
            Err(BadInput("Show must not start in the past")),
            result
        )
    }

    private class Fixtures(movies: Movies, shows: Shows, time: Instant) {
        val movieA = movies.addMovie(aMovie())
        val movieB = movies.addMovie(aMovie())

        val movieATomorrowShow = shows.addShow(aShow(movie = movieA, startTime = time + 1.days.toJavaDuration())).requireSucc()
        val movieANextWeekShow = shows.addShow(aShow(movie = movieA, startTime = time + 7.days.toJavaDuration())).requireSucc()
        val movieBDayAfterTomorrowShow = shows.addShow(aShow(movie = movieB, startTime = time + 2.days.toJavaDuration())).requireSucc()
        val movieBNextWeekShow = shows.addShow(aShow(movie = movieB, startTime = time + 8.days.toJavaDuration())).requireSucc()
    }
}