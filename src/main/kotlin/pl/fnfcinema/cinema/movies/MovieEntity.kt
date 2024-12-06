package pl.fnfcinema.cinema.movies

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY
import org.springframework.data.relational.core.mapping.Table
import pl.fnfcinema.cinema.EntityId
import java.util.*

@Table("movie")
data class MovieEntity(
    val title: String,
    val imdbId: String,
    @Embedded(onEmpty = USE_EMPTY, prefix = "rating_")
    val rating: Rating = Rating(),
    @Version val version: Int = 0,
    @Id val id: MovieId? = null,
) {
    fun rate(rate: Rate) = copy(
        rating = rating.record(rate)
    )
}

data class MovieId(override val value: UUID) : EntityId
