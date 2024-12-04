package pl.fnfcinema.cinema.integrations.imdb

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.movies.MovieDetails
import pl.fnfcinema.cinema.movies.MovieDetailsProvider

@Service
class ImdbMovieDetailsProvider(private val imdbApi: ImdbApi) : MovieDetailsProvider {

    @Cacheable(ImdbMoviesCacheConfig.CACHE_NAME)
    override fun fetchDetails(id: String): MovieDetails {
        val imdbMovie = imdbApi.fetchMovieById(id)
        return MovieDetails(
            imdbMovie.title,
            imdbMovie.releaseDate,
            imdbMovie.runtime,
            imdbMovie.genre,
            imdbMovie.director,
            imdbMovie.imdbRating,
            IMDB_RATING_SCALE,
            imdbMovie.imdbVotes.value,
            imdbMovie.poster,
            imdbMovie.awards,
        )
    }

    companion object {
        private const val IMDB_RATING_SCALE: Int = 10
    }
}