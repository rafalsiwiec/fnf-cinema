package pl.fnfcinema.cinema.integrations.imdb

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.notFoundResponse
import org.mockserver.model.HttpResponse.response
import org.springframework.web.reactive.function.client.WebClientException
import pl.fnfcinema.cinema.MockServerExtension
import java.io.File
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import kotlin.test.assertEquals


@ExtendWith(MockServerExtension::class)
class ImdbApiTest {

    @Test
    fun should_pass_movieId_together_with_apikey_and_parse_response() {
        // given
        val server = MockServerExtension.mockServerClient()
        server.`when`(request("/"))
            .respond(
                response()
                    .withHeader("content-type", "application/json")
                    .withBody(File("src/test/data/imdb/example.json").readText())
            )

        val config = ImdbIntegrationConfig(
            MockServerExtension.serverUri(),
            "api-key-123"
        )
        val imdbApi = ImdbApiFactory.create(config)

        // when
        val imdbMovie = imdbApi.fetchMovieById("movie-id")

        // then
        assertEquals(
            ImdbMovie(
                "The Fast and the Furious",
                LocalDate.of(2001, 6, 22),
                "106 min",
                "Action, Crime, Thriller",
                "Rob Cohen",
                BigDecimal("6.8"),
                ImdbInt(427944),
                URI.create("https://m.media-amazon.com/images/M/MV5BZGRiMDE1NTMtMThmZS00YjE4LWI1ODQtNjRkZGZlOTg2MGE1XkEyXkFqcGc@._V1_SX300.jpg"),
                "11 wins & 18 nominations"
            ),
            imdbMovie
        )
        server.verify(
            request("/")
                .withQueryStringParameter("apikey", "api-key-123")
                .withQueryStringParameter("i", "movie-id")
        )
    }

    @Test
    fun should_throw_error_when_status_code_is_not_2xx() {
        // given
        val server = MockServerExtension.mockServerClient()
        server.`when`(request("/"))
            .respond(notFoundResponse().withBody("askdjjkfdhasjhdfs"))

        val config = ImdbIntegrationConfig(
            MockServerExtension.serverUri(),
            "api-key-123"
        )
        val imdbApi = ImdbApiFactory.create(config)

        // when
        val error = assertThrows<ImdbApiError> { imdbApi.fetchMovieById("movie-id") }
        assertInstanceOf<WebClientException>(error.cause)
    }
}

