package pl.fnfcinema.cinema.movies

import org.springframework.stereotype.Service
import java.util.*

@Service
class Movies(
    private val movieDetailsProvider: MovieDetailsProvider
) {

    fun getAll(): List<MovieEntity> =
        FastAndFurious.allReleases.sortedBy { it.releaseNumber }

    fun getMovieDetails(id: UUID): MovieDetails? {
        val movie = FastAndFurious.allReleases.find { it.id == id }
        return movie?.let { movieDetailsProvider.fetchDetails(it.externalId) }
    }

}

private object FastAndFurious {
    val allReleases = listOf(
        MovieEntity("The Fast and the Furious", 1, "tt0232500", UUID.fromString("3a2e9a9c-8d84-4a6c-b752-bf3c514f5fa5")),
        MovieEntity("2 Fast 2 Furious", 2, "tt0322259", UUID.fromString("06bf4826-d6e1-42ff-85ce-18fbb9f11df6")),
        MovieEntity("The Fast and the Furious: Tokyo Drift", 3, "tt0463985", UUID.fromString("bd912943-7fb3-42c7-b48f-8c92dd0ace85")),
        MovieEntity("Fast & Furious", 4, "tt1013752", UUID.fromString("14317f05-8cd9-469a-9f16-7ff9ddb6672a")),
        MovieEntity("Fast Five", 5, "tt1596343", UUID.fromString("b83436db-cbc7-4c6f-8506-129d845f5ff5")),
        MovieEntity("Fast & Furious 6", 6, "tt1905041", UUID.fromString("f9ec3a73-899b-45da-821b-73b12ea2f664")),
        MovieEntity("Furious 7", 7, "tt2820852", UUID.fromString("8abee5c7-ad72-4e09-9b8d-9dd1f0c08911")),
        MovieEntity("The Fate of the Furious", 8, "tt4630562", UUID.fromString("51961e5d-2870-434c-82f7-b695b178d55a")),
        MovieEntity("F9: The Fast Saga", 9,"tt5433138", UUID.fromString("950142cf-a417-4238-a225-f9283844f17d"))
    )
}
