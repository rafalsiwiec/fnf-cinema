package pl.fnfcinema.cinema.movies

import org.junit.jupiter.api.Test
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import pl.fnfcinema.cinema.IntegrationTest
import pl.fnfcinema.cinema.integrations.imdb.ImdbMovieFixtures.anImdbMovie
import kotlin.test.assertEquals

class MoviesTest(
    @Autowired val movies: Movies
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
        `when`(imdbApi.fetchMovieById(imdbId)).thenReturn(imdbMovie)

        // when
        val firstResult = movies.getMovieDetails(savedMovie.id!!)

        // then
        verify(imdbApi).fetchMovieById(imdbId)
        verifyNoMoreInteractions(imdbApi)
        reset(imdbApi)

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
        verifyNoInteractions(imdbApi)
    }
}