package pl.fnfcinema.cinema.integrations.imdb

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "integrations.imdb")
data class ImdbIntegrationConfig(
    val baseUrl: URI,
    val apiKey: String
)
