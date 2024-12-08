package pl.fnfcinema.cinema.movies

import jakarta.annotation.PostConstruct
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.OptimisticLocking.rerunOnConflict
import pl.fnfcinema.cinema.Res
import pl.fnfcinema.cinema.Succ
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Service
class Movies(
    private val repo: MovieRepository,
    private val movieDetailsProvider: MovieDetailsProvider,
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
) {

    fun getAll(): List<MovieEntity> = repo.findAll().toList()

    fun getMovieDetails(id: MovieId): Pair<MovieEntity, MovieDetails>? =
        repo.findByIdOrNull(id)?.let {
            it to movieDetailsProvider.fetchDetails(it.imdbId)
        }

    fun addMovie(newMovie: MovieEntity): MovieEntity = repo.save(newMovie)

    fun findMovie(id: MovieId): MovieEntity? = repo.findByIdOrNull(id)

    fun rate(id: MovieId, rateValue: Int): Res<MovieEntity, Errors.MoviesError> {
        val rate = Rate.fromInt(rateValue) ?: return Err(Errors.BadInput("Invalid rate value"))

        return rerunOnConflict(withinAtMax = 500.milliseconds) {
            val movie = repo.findByIdOrNull(id) ?: return@rerunOnConflict Err(Errors.MovieNotFound(id))
            Succ(repo.save(movie.rate(rate)))
        }
    }

    @PostConstruct
    private fun initFastAndFuriousMovies() {
        FastAndFurious.allReleases.forEach {
            repo.findByIdOrNull(MovieId(it.id)) ?: jdbcAggregateTemplate.insert(it.toMovieEntity())
        }
    }

    object Errors {
        sealed interface MoviesError
        data class BadInput(val details: String) : MoviesError
        data class MovieNotFound(val id: MovieId) : MoviesError
    }
}

private object FastAndFurious {

    data class FastAndFuriousMovie(val title: String, val imdbId: String, val id: UUID) {
        fun toMovieEntity(): MovieEntity = MovieEntity(
            title = title,
            imdbId = imdbId,
            rating = Rating(),
            id = MovieId(id)
        )
    }

    val allReleases = listOf(
        FastAndFuriousMovie(
            "The Fast and the Furious",
            "tt0232500",
            UUID.fromString("3a2e9a9c-8d84-4a6c-b752-bf3c514f5fa5")
        ),
        FastAndFuriousMovie(
            "2 Fast 2 Furious",
            "tt0322259",
            UUID.fromString("06bf4826-d6e1-42ff-85ce-18fbb9f11df6")
        ),
        FastAndFuriousMovie(
            "The Fast and the Furious: Tokyo Drift",
            "tt0463985",
            UUID.fromString("bd912943-7fb3-42c7-b48f-8c92dd0ace85")
        ),
        FastAndFuriousMovie(
            "Fast & Furious",
            "tt1013752",
            UUID.fromString("14317f05-8cd9-469a-9f16-7ff9ddb6672a")
        ),
        FastAndFuriousMovie(
            "Fast Five",
            "tt1596343",
            UUID.fromString("b83436db-cbc7-4c6f-8506-129d845f5ff5")
        ),
        FastAndFuriousMovie(
            "Fast & Furious 6",
            "tt1905041",
            UUID.fromString("f9ec3a73-899b-45da-821b-73b12ea2f664")
        ),
        FastAndFuriousMovie(
            "Furious 7",
            "tt2820852",
            UUID.fromString("8abee5c7-ad72-4e09-9b8d-9dd1f0c08911")
        ),
        FastAndFuriousMovie(
            "The Fate of the Furious",
            "tt4630562",
            UUID.fromString("51961e5d-2870-434c-82f7-b695b178d55a")
        ),
        FastAndFuriousMovie(
            "F9: The Fast Saga",
            "tt5433138",
            UUID.fromString("950142cf-a417-4238-a225-f9283844f17d")
        ),
    )

}