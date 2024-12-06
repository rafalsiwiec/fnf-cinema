package pl.fnfcinema.cinema.movies

import org.junit.jupiter.api.Test
import pl.fnfcinema.cinema.BaseTest
import kotlin.test.assertEquals

class MovieEntityTest : BaseTest() {

    @Test
    fun `should record rating`() {
        // given
        val rating = Rating(10, 50)
        val movie = aMovie(rating = rating)

        // when
        val updatedMovie = movie.rate(Rate(1))

        // then
        assertEquals(
            movie.copy(rating = rating.record(Rate(1))),
            updatedMovie
        )
    }
}