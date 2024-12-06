package pl.fnfcinema.cinema.shows

import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.fnfcinema.cinema.Api.asErrorResponse
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowsError
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
            is Succ<ShowEntity> -> ResponseEntity.status(201).body(result.value.toShow())
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }

    @PutMapping("/{id}")
    fun updateShow(
        @PathVariable("id") id: UUID,
        @RequestBody showUpdate: Requests.ShowUpdate,
    ): ResponseEntity<Responses.Show> {
        val (startTime, ticketPrice) = showUpdate
        return when (val result = shows.updateShow(showId = id, startTime = startTime, ticketPrice = ticketPrice)) {
            is Succ<ShowEntity> -> ResponseEntity.ok(result.value.toShow())
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteShow(@PathVariable("id") id: UUID): ResponseEntity<Unit> =
        when (val result = shows.deleteShow(showId = id)) {
            is Succ<Unit> -> ResponseEntity.noContent().build()
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }

    @GetMapping
    fun findNearestShows(
        @RequestParam(required = false, name = "movie") movieId: UUID?,
        @RequestParam(name = "limit", defaultValue = "10") limit: Int,
    ): ResponseEntity<List<Responses.Show>> =
        when (val result = shows.findNearest(movieId, limit)) {
            is Succ<List<ShowEntity>> -> ResponseEntity.ok(result.value.map { it.toShow() })
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }

    object Requests {
        data class NewShow(val movieId: UUID, val startTime: Instant, val ticketPrice: Money)
        data class ShowUpdate(val startTime: Instant, val ticketPrice: Money)
    }

    object Responses {
        data class Show(val id: UUID, val startTime: Instant)
    }

    companion object {
        private fun Requests.NewShow.toEntity() =
            ShowEntity(AggregateReference.to(this.movieId), startTime, ticketPrice)

        private fun ShowEntity.toShow() =
            Responses.Show(id!!, startTime)

        private fun errorDetails(err: ShowsError): Pair<Int, String> =
            when (err) {
                is Shows.Errors.BadInput -> 400 to err.details
                is Shows.Errors.ShowNotFound -> 404 to "Show with id: ${err.id} not found"
            }
    }
}