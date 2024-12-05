package pl.fnfcinema.cinema.movies

import io.github.serpro69.kfaker.Faker
import java.util.*

object MovieEntityFixtures {

    private val faker = Faker()

    fun aMovie(id: UUID? = null) = MovieEntity(
        faker.movie.title(),
        faker.string.numerify("tt#######"),
        id
    )
}