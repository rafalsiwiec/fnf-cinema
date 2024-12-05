package pl.fnfcinema.cinema

import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import pl.fnfcinema.cinema.movies.Movies
import pl.fnfcinema.cinema.shows.Shows

@WebMvcTest
abstract class ApiTest {

    @MockkBean lateinit var movies: Movies
    @MockkBean lateinit var shows: Shows

    @AfterEach
    fun tearDown() {
        confirmVerified(movies, shows)
    }
}