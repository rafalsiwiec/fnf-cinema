package pl.fnfcinema.cinema.movies

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.Security.X_STAFF_USER_ID_HEADER
import pl.fnfcinema.cinema.BaseApiTest
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.OptimisticLocking
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.aMovie
import pl.fnfcinema.cinema.aMovieDetails
import pl.fnfcinema.cinema.aRate
import pl.fnfcinema.cinema.aStaffUserId
import pl.fnfcinema.cinema.movies.Movies.Errors.BadInput
import pl.fnfcinema.cinema.movies.Movies.Errors.MovieNotFound
import pl.fnfcinema.cinema.movies.MoviesApi.AddMovieReq
import pl.fnfcinema.cinema.movies.MoviesApi.BasicMovieRes
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals

class MoviesApiTest : BaseApiTest() {

    @Test
    fun should_add_movie() {
        // given
        val addMovieReqReq = AddMovieReq("some title", "tt0000006")
        val newMovie = MovieEntity(addMovieReqReq.title, addMovieReqReq.imdbId)
        val newMovieId = UUID.randomUUID()
        val savedMovie = newMovie.copy(id = MovieId(newMovieId))

        every { movies.addMovie(any()) } returns savedMovie

        // when
        val response = mockMvc.perform(
            post("/movies")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
                .content(json.writeValueAsBytes(addMovieReqReq))
        ).andReturn().response

        // then
        verify { movies.addMovie(newMovie) }

        assertEquals(201, response.status)
        assertEquals(
            BasicMovieRes(newMovieId, savedMovie.title),
            json.parse(response)
        )
    }

    @Test
    fun `should secure create movie endpoint and require valid staff-user-id`() {
        // given
        val addMovieReq = AddMovieReq("some title", "tt0000006")
        listOf(
            post("/movies")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, "invalid-staff-user-id")
                .content(json.writeValueAsBytes(addMovieReq)),
            post("/movies")
                .contentType(APPLICATION_JSON)
                .content(json.writeValueAsBytes(addMovieReq)),
        ).forEach { invalidReq ->
            // when
            val response = mockMvc.perform(invalidReq).andReturn().response
            // then
            assertEquals(403, response.status)
        }
    }

    @Test
    fun `should return all movies`() {
        // given
        val firstMovie = aMovie(id = MovieId(UUID.randomUUID()))
        val secondMovie = aMovie(id = MovieId(UUID.randomUUID()))

        every { movies.getAll() } returns listOf(firstMovie, secondMovie)

        // when
        val response = mockMvc.perform(get("/movies")).andReturn().response

        // then
        assertEquals(200, response.status)
        assertEquals("application/json", response.getHeader("content-type"))
        assertEquals(
            listOf(
                BasicMovieRes(firstMovie.id!!.value, firstMovie.title),
                BasicMovieRes(secondMovie.id!!.value, secondMovie.title)
            ),
            json.parseList(response)
        )

        verify { movies.getAll() }
    }

    @Test
    fun should_respond_with_http_404_when_movie_with_given_id_is_not_found() {
        // given
        val id = MovieId(UUID.randomUUID())

        every { movies.getMovieDetails(id) } returns Err(Movies.Errors.MovieNotFound(id))

        // when
        val response = mockMvc.perform(get("/movies/${id.value}")).andReturn().response

        // then
        assertEquals(404, response.status)

        verify { movies.getMovieDetails(id) }
    }

    @Test
    fun `should rate movie`() {
        // given
        val rate = aRate()
        val movieId = MovieId(UUID.randomUUID())
        val movie = aMovie(id = movieId)
        val updatedMovie = movie.rate(rate)

        every { movies.rate(MovieId(any()), any()) } returns Succ(updatedMovie)

        // when
        val response = mockMvc.perform(
            post("/movies/${movieId.value}/rating/${rate.value}")
        ).andReturn().response

        // then
        verify { movies.rate(movieId, rate.value) }
        assertEquals(200, response.status)
        assertEquals(
            BasicMovieRes(
                id = movieId.value,
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
        val unknownMovieId = MovieId(UUID.randomUUID())

        every { movies.rate(MovieId(any()), any()) } returns Err(MovieNotFound(unknownMovieId))

        // when
        val response = mockMvc.perform(
            post("/movies/${unknownMovieId.value}/rating/3")
        ).andReturn().response

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
        val movieId = MovieId(UUID.randomUUID())

        every { movies.rate(MovieId(any()), any()) } returns Err(BadInput("Something went wrong"))

        // when
        val response = mockMvc.perform(post("/movies/${movieId.value}/rating/3")).andReturn().response

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
        val movieId = MovieId(UUID.randomUUID())

        every { movies.rate(MovieId(any()), any()) } throws OptimisticLocking.ConflictError()

        // when
        val response = mockMvc.perform(post("/movies/${movieId.value}/rating/3")).andReturn().response

        // then
        verify { movies.rate(movieId, 3) }
        assertEquals(409, response.status)
    }

    @Test
    fun `should return movie details`() {
        // given
        val movieId = UUID.randomUUID()
        val movie = aMovie(id = MovieId(movieId), rating = Rating(1234, 2468))
        val movieDetails = aMovieDetails()

        every { movies.getMovieDetails(MovieId(any())) } returns Succ(movie to movieDetails)

        // when
        val response = mockMvc.perform(get("/movies/$movieId")).andReturn().response

        // then
        verify { movies.getMovieDetails(MovieId(movieId)) }

        assertEquals(200, response.status)
        assertEquals(
            MoviesApi.MovieDetailsRes(
                id = movieId,
                title = movie.title,
                rating = MoviesApi.Rating("fnfcinema", 1234, BigDecimal("2.0"), 5),
                details = MoviesApi.Details(
                    releaseDate = movieDetails.releaseDate,
                    runtime = movieDetails.runtime,
                    genre = movieDetails.genre,
                    director = movieDetails.director,
                    posterUrl = movieDetails.posterUrl,
                    awards = movieDetails.awards,
                    ratings = listOf(
                        MoviesApi.Rating("imdb", movieDetails.votes, movieDetails.rating, movieDetails.ratingScale),
                    )
                )
            ),
            json.parse(response)
        )
    }

    @Test
    fun `should respond http 404 when movie not found`() {
        // given
        val movieId = UUID.randomUUID()

        every { movies.getMovieDetails(MovieId(any())) } returns Err(Movies.Errors.MovieNotFound(MovieId(movieId)))

        // when
        val response = mockMvc.perform(get("/movies/$movieId")).andReturn().response

        // then
        verify { movies.getMovieDetails(MovieId(movieId)) }

        assertEquals(404, response.status)
    }
}