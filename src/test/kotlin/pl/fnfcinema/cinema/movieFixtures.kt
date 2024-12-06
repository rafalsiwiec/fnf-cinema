package pl.fnfcinema.cinema

import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.movies.Rate
import pl.fnfcinema.cinema.movies.Rating

fun aMovie(
    title: String = faker.movie.title(),
    imdbId: String = anImdbId(),
    rating: Rating = Rating(),
    id: MovieId? = null,
) = MovieEntity(
    title = title,
    imdbId = imdbId,
    rating = rating,
    id = id,
)

fun aRate(value: Int = faker.random.nextInt(1, 5)): Rate = Rate(value = value)