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
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.ApiTest
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.OptimisticLocking
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.aMovie
import pl.fnfcinema.cinema.aRate
import pl.fnfcinema.cinema.aTicketPrice
import pl.fnfcinema.cinema.movies.Movies.Errors.BadInput
import pl.fnfcinema.cinema.movies.Movies.Errors.MovieNotFound
import pl.fnfcinema.cinema.movies.MoviesApi.Requests.NewMovie
import pl.fnfcinema.cinema.movies.MoviesApi.Responses.BasicMovie
import java.util.*
import kotlin.test.assertEquals

class MoviesApiTest(
    @Autowired val mockMvc: MockMvc,
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
                .content(json.writeValueAsBytes(newMovieReq))
        ).andReturn().response

        // then
        verify { movies.addMovie(newMovie) }

        assertEquals(201, response.status)
        val responseBody = json.readValue(response.contentAsByteArray, BasicMovie::class.java)
        assertEquals(BasicMovie(newMovieId, savedMovie.title), responseBody)
    }

    @Test
    fun should_search_movies() {
        // given
        val firstMovie = aMovie(id = UUID.randomUUID())
        val secondMovie = aMovie(id = UUID.randomUUID())

        every { movies.getAll() } returns listOf(firstMovie, secondMovie)

        // when
        val response = mockMvc.perform(get("/movies")).andReturn().response

        // then
        assertEquals(200, response.status)
        assertEquals("application/json", response.getHeader("content-type"))
        val foundMovies = json.readerFor(BasicMovie::class.java)
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

    @Test
    fun `should rate movie`() {
        // given
        val rate = aRate()
        val movieId = UUID.randomUUID()
        val movie = aMovie(id = movieId)
        val updatedMovie = movie.rate(rate)

        every { movies.rate(any(), any()) } returns Succ(updatedMovie)

        // when
        val response = mockMvc.perform(
            post("/movies/${movie.id}/rating/${rate.value}")
        ).andReturn().response

        // then
        verify { movies.rate(movieId, rate.value) }
        assertEquals(200, response.status)
        assertEquals(
            BasicMovie(
                id = movieId,
                title = movie.title,
                votes = updatedMovie.rating.votes,
                avgRate = updatedMovie.rating.avg()
            ),
            json.parse(response)
        )
    }

    @Test
    fun `should respond http 404 when rated movie does not exist`() {
        // given
        val unknownMovieId = UUID.randomUUID()

        every { movies.rate(any(), any()) } returns Err(MovieNotFound(unknownMovieId))

        // when
        val response = mockMvc.perform(post("/movies/$unknownMovieId/rating/3")).andReturn().response

        // then
        verify { movies.rate(unknownMovieId, 3) }
        assertEquals(404, response.status)
        assertEquals(
            Api.ErrorDetails("Movie with id: $unknownMovieId not found"),
            json.parse(response)
        )
    }

    @Test
    fun `should respond http 400 when rating operation fails because of bad input`() {
        // given
        val movieId = UUID.randomUUID()

        every { movies.rate(any(), any()) } returns Err(BadInput("Something went wrong"))

        // when
        val response = mockMvc.perform(post("/movies/$movieId/rating/3")).andReturn().response

        // then
        verify { movies.rate(movieId, 3) }
        assertEquals(400, response.status)
        assertEquals(
            Api.ErrorDetails("Something went wrong"),
            json.parse(response)
        )
    }

    @Test
    fun `should respond http 409 when rating operation throws conflict`() {
        // given
        val movieId = UUID.randomUUID()

        every { movies.rate(any(), any()) } throws OptimisticLocking.ConflictError()

        // when
        val response = mockMvc.perform(post("/movies/$movieId/rating/3")).andReturn().response

        // then
        verify { movies.rate(movieId, 3) }
        assertEquals(409, response.status)
    }
}