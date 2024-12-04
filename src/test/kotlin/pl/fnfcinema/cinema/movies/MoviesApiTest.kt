package pl.fnfcinema.cinema.movies

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import pl.fnfcinema.cinema.integrations.imdb.ImdbMovieFixtures.anImdbMovie
import pl.fnfcinema.cinema.movies.MoviesApi.Responses.BasicMovie
import java.util.*
import kotlin.test.assertEquals

class MoviesApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) : IntegrationTest() {

    @Test
    fun should_search_movies() {
        // when
        val response = mockMvc.perform(get("/movies")).andReturn().response

        // then
        assertEquals(200, response.status)
        assertEquals("application/json", response.getHeader("content-type"))
        val foundMovies = objectMapper.readerFor(BasicMovie::class.java)
            .readValues<BasicMovie>(response.contentAsByteArray)
            .readAll()
        assertEquals(9, foundMovies.size)
        assertEquals(
            BasicMovie(UUID.fromString("950142cf-a417-4238-a225-f9283844f17d"), "F9: The Fast Saga"),
            foundMovies.last()
        )
    }

    @Test
    fun should_return_movie_details_fetched_from_external_provider() {
        // given
        val fastAndFurious6Id = UUID.fromString("f9ec3a73-899b-45da-821b-73b12ea2f664")
        val imdbMovie = anImdbMovie()

        `when`(imdbApi.fetchMovieById("tt1905041")).thenReturn(imdbMovie)

        // when
        val response = mockMvc.perform(get("/movies/{id}", fastAndFurious6Id)).andReturn().response

        // then
        assertEquals(200, response.status)
        val movieDetails = objectMapper.readValue(response.contentAsByteArray, MovieDetails::class.java)
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
                imdbMovie.awards
            ),
            movieDetails
        )

        verify(imdbApi).fetchMovieById("tt1905041")
        verifyNoMoreInteractions(imdbApi)
    }

    @Test
    fun should_respond_with_http_404_when_movie_with_given_id_is_not_found() {
        // when
        val response = mockMvc.perform(get("/movies/{id}", UUID.randomUUID())).andReturn().response

        // then
        assertEquals(404, response.status)
    }
}