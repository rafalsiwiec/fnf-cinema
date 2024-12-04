package pl.fnfcinema.cinema.integrations.imdb

import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient

interface ImdbApi {

    fun fetchMovieById(id: String): ImdbMovie

}

@Configuration
class ImdbApiFactory {
    companion object {
        @Bean
        @JvmStatic
        fun create(config: ImdbIntegrationProperties): ImdbApi = ImdbApiImpl(config)
    }
}

private class ImdbApiImpl(
    private val config: ImdbIntegrationProperties,
) : ImdbApi {

    private val logger = KotlinLogging.logger {}

    private val client = WebClient.builder()
        .baseUrl(config.baseUrl.toString())
        .build()

    override fun fetchMovieById(id: String): ImdbMovie =
        try {
            logger.info { "Will fetch imdbMovie with id: $id" }
            client.get()
                .uri {
                    it.queryParam("apikey", config.apiKey)
                        .queryParam("i", id)
                        .build()
                }
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ImdbMovie::class.java)
                .block() ?: throw ImdbApiError("Unable to retrieve payload")
        } catch (e: Exception) {
            throw ImdbApiError(e)
        }
}