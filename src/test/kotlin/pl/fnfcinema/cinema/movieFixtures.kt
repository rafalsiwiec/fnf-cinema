package pl.fnfcinema.cinema

import pl.fnfcinema.cinema.movies.MovieDetails
import pl.fnfcinema.cinema.movies.MovieEntity
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.movies.Rate
import pl.fnfcinema.cinema.movies.Rating
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

fun aMovieTitle() = faker.movie.title()

fun aMovie(
    title: String = aMovieTitle(),
    imdbId: String = anImdbId(),
    rating: Rating = Rating(),
    id: MovieId? = null,
) = MovieEntity(
    title = title,
    imdbId = imdbId,
    rating = rating,
    id = id,
)

fun aRuntime() = "${faker.random.nextInt(30..180)} mins"

fun aMovieDetails(
    releaseDate: LocalDate = aPastDate(),
    runtime: String = aRuntime(),
    genre: String = "",
    director: String = faker.name.firstName() + " " + faker.name.lastName(),
    rating: BigDecimal = BigDecimal(faker.random.nextInt(1..10)),
    ratingScale: Int = 10,
    votes: Long = faker.random.nextLong(100000),
    posterUrl: URI = URI.create("http://some-fake-url.abc/image.jpg"),
    awards: String = ""
) = MovieDetails(
    releaseDate = releaseDate,
    runtime = runtime,
    genre = genre,
    director = director,
    rating = rating,
    ratingScale = ratingScale,
    votes = votes,
    posterUrl = posterUrl,
    awards = awards,
)

fun aRate(value: Int = faker.random.nextInt(1, 5)): Rate = Rate(value = value)