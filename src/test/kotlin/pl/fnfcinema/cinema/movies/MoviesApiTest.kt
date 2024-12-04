package pl.fnfcinema.cinema.movies

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import pl.fnfcinema.cinema.movies.MoviesApi.Responses.Movie
import java.util.*
import kotlin.test.assertEquals

class MoviesApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) : IntegrationTest() {

    @Test
    fun should_search_movies() {
        // given
        val searchQuery = "f9"

        // when
        val response = mockMvc.perform(get("/movies")).andReturn().response

        // then
        assertEquals(200, response.status)
        assertEquals("application/json", response.getHeader("content-type"))
        val foundMovies = objectMapper.readerFor(Movie::class.java)
            .readValues<Movie>(response.contentAsByteArray)
            .readAll()
        assertEquals(9, foundMovies.size)
        assertEquals(
            Movie(UUID.fromString("950142cf-a417-4238-a225-f9283844f17d"), "F9: The Fast Saga"),
            foundMovies.last()
        )
    }
}