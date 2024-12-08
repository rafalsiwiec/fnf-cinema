package pl.fnfcinema.cinema

import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EnabledIfSystemProperty(named = "tests.endtoend", matches = "true")
class EndToEndTest(
    @Autowired @LocalServerPort private val port: Int,
) {

    init {
        require(System.getenv("IMDB_API_KEY").isNotBlank(), { "IMDB_API_KEY env variable is not set" })
    }

    private val furious7Id = "8abee5c7-ad72-4e09-9b8d-9dd1f0c08911"

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `should fetch given movie details from IMDB api`() {
        When {
            get("/movies/$furious7Id")
        } Then {
            statusCode(200)
            contentType(JSON)
            body("id", equalTo(furious7Id))
            body("title", equalTo("Furious 7"))

            body("rating.origin", equalTo("fnfcinema"))
            body("rating.votes", equalTo(0))
            body("rating.avgRate", equalTo(null))
            body("rating.scale", equalTo(5))

            body("details.releaseDate", equalTo("2015-04-03"))
            body("details.runtime", equalTo("137 min"))
            body("details.genre", equalTo("Action, Crime, Thriller"))
            body("details.director", equalTo("James Wan"))
            body(
                "details.posterUrl",
                equalTo("https://m.media-amazon.com/images/M/MV5BMTQxOTA2NDUzOV5BMl5BanBnXkFtZTgwNzY2MTMxMzE@._V1_SX300.jpg")
            )
            body("details.awards", equalTo("36 wins & 36 nominations"))

            body("details.ratings", Matchers.hasSize<List<*>>(1))
            body("details.ratings[0].origin", equalTo("imdb"))
            body("details.ratings[0].votes", greaterThanOrEqualTo(42000))
            body("details.ratings[0].avgRate", isA<Float>(Float::class.java))
            body("details.ratings[0].scale", equalTo(10))
        }
    }
}