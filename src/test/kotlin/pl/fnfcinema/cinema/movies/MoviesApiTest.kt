package pl.fnfcinema.cinema.movies

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import pl.fnfcinema.cinema.ApiTest
import pl.fnfcinema.cinema.movies.MoviesApi.Requests.NewMovie
import pl.fnfcinema.cinema.movies.MoviesApi.Responses.BasicMovie
import java.util.*
import kotlin.test.assertEquals

class MoviesApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) : ApiTest() {

    @Test
    fun should_add_movie() {
        // given
        val newMovieReq = NewMovie("some title", "tt0000006", aTicketPrice())
        val newMovie = MovieEntity(newMovieReq.title, newMovieReq.imdbId)
        val newMovieId = UUID.randomUUID()
        val savedMovie = newMovie.copy(id = newMovieId)

        every { movies.addMovie(any()) } returns savedMovie

        // when
        val response = mockMvc.perform(
            post("/movies")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newMovieReq))
        ).andReturn().response

        // then
        verify { movies.addMovie(newMovie) }

        assertEquals(201, response.status)
        val responseBody = objectMapper.readValue(response.contentAsByteArray, BasicMovie::class.java)
        assertEquals(BasicMovie(newMovieId, savedMovie.title), responseBody)
    }

    @Test
    fun should_search_movies() {
        // given
        val firstMovie = MovieEntity("some-title-1", "tt0000001", UUID.randomUUID())
        val secondMovie = MovieEntity("some-title-2", "tt0000002", UUID.randomUUID())

        every { movies.getAll() } returns listOf(firstMovie, secondMovie)

        // when
        val response = mockMvc.perform(get("/movies")).andReturn().response

        // then
        assertEquals(200, response.status)
        assertEquals("application/json", response.getHeader("content-type"))
        val foundMovies = objectMapper.readerFor(BasicMovie::class.java)
            .readValues<BasicMovie>(response.contentAsByteArray)
            .readAll()
        assertEquals(2, foundMovies.size)
        assertEquals(
            listOf(
                BasicMovie(firstMovie.id!!, firstMovie.title),
                BasicMovie(secondMovie.id!!, secondMovie.title)
            ),
            foundMovies
        )

        verify { movies.getAll() }
    }

    @Test
    fun should_respond_with_http_404_when_movie_with_given_id_is_not_found() {
        // given
        val id = UUID.randomUUID()

        every { movies.getMovieDetails(id) } returns null

        // when
        val response = mockMvc.perform(get("/movies/{id}", id)).andReturn().response

        // then
        assertEquals(404, response.status)

        verify { movies.getMovieDetails(id) }
    }
}