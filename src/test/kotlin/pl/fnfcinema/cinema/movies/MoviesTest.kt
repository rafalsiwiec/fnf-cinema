package pl.fnfcinema.cinema.movies

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import pl.fnfcinema.cinema.BaseIntegrationTest
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.aMovie
import pl.fnfcinema.cinema.anImdbMovie
import pl.fnfcinema.cinema.movies.Movies.Errors.BadInput
import pl.fnfcinema.cinema.requireSucc
import java.util.*
import kotlin.test.assertEquals

class MoviesTest(
    @Autowired val movies: Movies,
) : BaseIntegrationTest() {

    @Test
    fun should_add_new_movie_and_then_return() {
        // given
        val newMovie = MovieEntity("some title", "tt0000001")

        // when
        val addedMovie = movies.addMovie(newMovie)

        // when
        val results = movies.getAll()

        // then
        assertEquals(
            addedMovie,
            results.find { it.id == addedMovie.id }
        )
    }

    @Test
    fun should_cache_movie_details_fetched_from_provider() {
        // given
        val imdbId = "tt2820852"
        val savedMovie = movies.addMovie(MovieEntity("title 123", imdbId))

        val imdbMovie = anImdbMovie()
        every { imdbApi.fetchMovieById(imdbId) } returns imdbMovie

        // when
        val firstResult = movies.getMovieDetails(savedMovie.id!!)

        // then
        verify(exactly = 1) { imdbApi.fetchMovieById(imdbId) }
        confirmVerified(imdbApi)

        assertEquals(
            savedMovie to MovieDetails(
                imdbMovie.releaseDate,
                imdbMovie.runtime,
                imdbMovie.genre,
                imdbMovie.director,
                imdbMovie.imdbRating,
                10,
                imdbMovie.imdbVotes.value,
                imdbMovie.poster,
                imdbMovie.awards,
            ),
            firstResult
        )

        // when
        val secondResult = movies.getMovieDetails(savedMovie.id!!)

        // then
        assertEquals(firstResult, secondResult)

        // no additional call to imdbApi was performed
        confirmVerified(imdbApi)
    }

    @Test
    fun `should rate movie`() {
        // given
        val movie = movies.addMovie(aMovie())

        // when
        val movieAfterFirstRate = movies.rate(movie.id!!, 5).requireSucc()

        // then
        assertEquals(
            Rating(1, 5),
            movieAfterFirstRate.rating
        )

        // when
        val movieAfterSecondRate = movies.rate(movie.id!!, 3).requireSucc()

        // then
        assertEquals(
            Rating(2, 8),
            movieAfterSecondRate.rating
        )

        // when
        val movieAfterThirdRate = movies.rate(movie.id!!, 4).requireSucc()

        // then
        assertEquals(
            Rating(3, 12),
            movieAfterThirdRate.rating
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 6, 10])
    fun `should validate provided rate to be in allowed range`(invalidRateValue: Int) {
        // given
        val movieId = MovieId(UUID.randomUUID())
        movies.addMovie(aMovie(id = movieId))

        // when
        val result = movies.rate(movieId, invalidRateValue)

        // then
        assertEquals(
            Err(BadInput("Invalid rate value")),
            result
        )
    }

    @Test
    fun `should return error result on rating when movie does not exist`() {
        // given
        val unknownMovieId = MovieId(UUID.randomUUID())

        // when
        val result = movies.rate(unknownMovieId, 3)

        // then
        assertEquals(
            Err(Movies.Errors.MovieNotFound(unknownMovieId)),
            result
        )
    }
}