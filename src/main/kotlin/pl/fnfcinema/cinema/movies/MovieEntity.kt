package pl.fnfcinema.cinema.movies

import java.util.*

data class MovieEntity(
    val title: String,
    val releaseNumber: Int,
    val externalId: String,
    val id: UUID
)
