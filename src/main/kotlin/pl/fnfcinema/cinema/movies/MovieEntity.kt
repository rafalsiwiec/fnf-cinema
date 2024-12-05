package pl.fnfcinema.cinema.movies

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("movie")
data class MovieEntity(
    val title: String,
    val imdbId: String,
    @Id val id: UUID? = null
)
