package pl.fnfcinema.cinema.movies

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.fnfcinema.cinema.IntegrationTest
import kotlin.test.assertEquals

class MoviesTest(
    @Autowired val movies: Movies,
) : IntegrationTest() {

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
            MovieDetails(
                imdbMovie.title,
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
        val movieAfterFirstRate = movies.rate(movie.id!!, 5)

        // then
        assertEquals(
            Rating(1, 5),
            movieAfterFirstRate?.rating
        )

        // when
        val movieAfterSecondRate = movies.rate(movie.id!!, 3)

        // then
        assertEquals(
            Rating(2, 8),
            movieAfterSecondRate?.rating
        )

        // when
        val movieAfterThirdRate = movies.rate(movie.id!!, 4)

        // then
        assertEquals(
            Rating(3, 12),
            movieAfterThirdRate?.rating
        )
    }
}