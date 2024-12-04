package pl.fnfcinema.cinema.integrations.imdb

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class ImdbMoviesCacheConfig {

    @Bean
    fun imdbMoviesCacheManager(properties: ImdbMoviesCacheProperties): CacheManager {
        val cacheManager = CaffeineCacheManager(CACHE_NAME)
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(properties.expireAfter)
                .maximumSize(properties.maxSize)
        )
        return cacheManager
    }

    companion object {
        const val CACHE_NAME: String = "imdbMovies"
    }
}