package pl.fnfcinema.cinema.integrations.imdb

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("integrations.imdb.cache")
data class ImdbMoviesCacheProperties(val maxSize: Long, val expireAfter: Duration)