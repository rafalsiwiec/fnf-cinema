package pl.fnfcinema.cinema.movies

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import pl.fnfcinema.cinema.BaseIntegrationTest
import pl.fnfcinema.cinema.aMovie
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertEquals


@EnabledIfSystemProperty(named = "tests.multithreaded", matches = "true")
class MovieRatingMultiThreadedTest(
    @Autowired val movies: Movies,
) : BaseIntegrationTest() {

    lateinit var executor: ExecutorService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        executor = Executors.newFixedThreadPool(5)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        executor.shutdown()
        executor.awaitTermination(2, SECONDS)
    }

    @Test
    fun `should handle parallel ratings`() {
        // given
        val movie = movies.addMovie(aMovie())

        val numberOfRequests = 100

        // when
        val asyncUpdates = (1..numberOfRequests).map { (it % 4) + 1 }.map { rate ->
            CompletableFuture.supplyAsync(
                { movies.rate(movie.id!!, rate) },
                executor
            )
        }

        // then
        CompletableFuture.allOf(*asyncUpdates.toTypedArray()).join()
        executor.shutdown()
        assertEquals(
            Rating(numberOfRequests.toLong(), (numberOfRequests * 2.5).toLong()),
            movies.findMovie(movie.id!!)?.rating
        )
    }

}