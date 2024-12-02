package pl.fnfcinema.cinema.movies

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MoviesTest(
    @Autowired val movies: Movies
) : IntegrationTest() {

    @Test
    fun should_query_movies_by_title() {
        // when
        val firstResult = movies.findByTitle("7")

        // then
        assertEquals(1, firstResult.size)
        assertEquals("Furious 7", firstResult[0].title)
        assertEquals("tt2820852", firstResult[0].externalId)

        // when
        val secondResult = movies.findByTitle("fast")

        // then
        assertThat(secondResult.size, greaterThan(1))

        // when
        val thirdResult = movies.findByTitle("unknown title")

        // then
        assertEquals(0, thirdResult.size)
    }
}