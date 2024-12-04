package pl.fnfcinema.cinema.movies

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/movies")
class MoviesApi(private val movies: Movies) {

    @GetMapping
    fun getAllMovies() = movies.getAll().map { it.toApi() }

    companion object Responses {

        data class Movie(val id: UUID, val title: String)

        private fun MovieEntity.toApi(): Movie = Movie(id, title)
    }
}