package pl.fnfcinema.cinema.movies

import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.integrations.imdb.ImdbApi

@Service
class ImdbMovieDetailsProvider(private val imdbApi: ImdbApi) : MovieDetailsProvider<String> {

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