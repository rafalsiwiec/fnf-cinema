package pl.fnfcinema.cinema

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows

@WebMvcTest
abstract class BaseApiTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var json: ObjectMapper

    @MockkBean lateinit var movies: Movies
    @MockkBean lateinit var shows: Shows

    @AfterEach
    fun tearDown() {
        confirmVerified(movies, shows)
    }

    protected inline fun <reified T> ObjectMapper.parse(resp: MockHttpServletResponse): T =
        readValue(resp.contentAsByteArray, T::class.java)

    protected inline fun <reified T> ObjectMapper.parseList(resp: MockHttpServletResponse): List<T> =
        readerFor(T::class.java).readValues<T>(resp.contentAsByteArray).readAll()

}