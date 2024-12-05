package pl.fnfcinema.cinema.shows

import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Error
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Success
import pl.fnfcinema.cinema.shows.Shows.Errors.InvalidData
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowsActionError
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/shows")
class ShowsApi(
    private val shows: Shows,
) {

    @PostMapping
    fun addShow(@RequestBody newShow: Requests.NewShow): ResponseEntity<Responses.Show> =
        when (val result = shows.addShow(newShow.toEntity())) {
            is Success<ShowEntity> ->
                ResponseEntity.status(201).body(result.value.toShow())

            is Error<ShowsActionError> -> when (result.error) {
                is InvalidData ->
                    ResponseEntity.status(400).build()
            }
        }

    @GetMapping
    fun findNearestShows(
        @RequestParam(required = false, name = "movie") movieId: UUID?,
        @RequestParam(name = "limit", defaultValue = "10") limit: Int,
    ): ResponseEntity<List<Responses.Show>> = when(val result = shows.findNearest(movieId, limit)) {
        is Success<List<ShowEntity>> ->
            ResponseEntity.ok(result.value.map { it.toShow() })

        is Error<ShowsActionError> ->
            ResponseEntity.status(400).build()
    }

    object Requests {
        data class NewShow(val movieId: UUID, val startTime: Instant, val price: Money)
    }

    object Responses {
        data class Show(val id: UUID, val startTime: Instant)
    }

    companion object {
        private fun Requests.NewShow.toEntity() =
            ShowEntity(AggregateReference.to(this.movieId), startTime, price)

        private fun ShowEntity.toShow() =
            Responses.Show(id!!, startTime)
    }
}