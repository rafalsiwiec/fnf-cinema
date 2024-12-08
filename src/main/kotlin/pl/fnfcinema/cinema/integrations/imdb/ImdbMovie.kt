package pl.fnfcinema.cinema.integrations.imdb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate

data class ImdbMovie(
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("Released")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
    val releaseDate: LocalDate,
    @JsonProperty("Runtime")
    val runtime: String,
    @JsonProperty("Genre")
    val genre: String,
    @JsonProperty("Director")
    val director: String,
    val imdbRating: BigDecimal,
    val imdbVotes: ImdbLong,
    @JsonProperty("Poster")
    val poster: URI,
    @JsonProperty("Awards")
    val awards: String
)

data class ImdbLong(val value: Long) {
    @JsonCreator
    constructor(strValue: String) : this(strValue.replace(",", "").toLong())
}