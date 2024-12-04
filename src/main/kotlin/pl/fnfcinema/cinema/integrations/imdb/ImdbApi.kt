package pl.fnfcinema.cinema.integrations.imdb

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
        fun create(config: ImdbIntegrationConfig): ImdbApi = ImdbApiImpl(config)
    }
}

private class ImdbApiImpl(
    private val config: ImdbIntegrationConfig,
) : ImdbApi {

    private val client = WebClient.builder()
        .baseUrl(config.baseUrl.toString())
        .build()

    override fun fetchMovieById(id: String): ImdbMovie =
        try {
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