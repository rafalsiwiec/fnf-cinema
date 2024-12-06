package pl.fnfcinema.cinema.shows

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.ALL_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
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
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.Security.STAFF_ONLY
import pl.fnfcinema.cinema.Api.asErrorResponse
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.StaffUserId
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowsError
import java.time.Instant
import java.util.*

@Tag(
    name = "Shows API",
    description = "Manages shows"
)
@RestController
@RequestMapping("/shows")
class ShowsApi(
    private val shows: Shows,
) {

    @SecurityRequirement(name = STAFF_ONLY)
    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun addShow(@RequestBody addShowReq: AddShowReq): ResponseEntity<ShowRes> {
        val staffUserId = Api.Security.requireStaffUserId()
        return when (val result = shows.addShow(addShowReq.toEntity(staffUserId))) {
            is Succ<ShowEntity> -> ResponseEntity.status(201).body(result.value.toShow())
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }
    }

    @SecurityRequirement(name = STAFF_ONLY)
    @PutMapping("/{id}", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun updateShow(
        @PathVariable("id") id: UUID,
        @RequestBody showUpdate: UpdateShowReq,
    ): ResponseEntity<ShowRes> {
        Api.Security.requireStaffUserId()
        val (startTime, ticketPrice) = showUpdate
        return when (val result =
            shows.updateShow(showId = ShowId(id), startTime = startTime, ticketPrice = ticketPrice.toMoney())) {
            is Succ<ShowEntity> -> ResponseEntity.ok(result.value.toShow())
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }
    }

    @SecurityRequirement(name = STAFF_ONLY)
    @DeleteMapping("/{id}", produces = [APPLICATION_JSON_VALUE])
    fun deleteShow(@PathVariable("id") id: UUID): ResponseEntity<Unit> {
        Api.Security.requireStaffUserId()
        return when (val result = shows.deleteShow(showId = ShowId(id))) {
            is Succ<Unit> -> ResponseEntity.noContent().build()
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun findNearestShows(
        @RequestParam(required = false, name = "movie") movieId: UUID?,
        @RequestParam(name = "limit", defaultValue = "10") limit: Int,
    ): ResponseEntity<List<ShowRes>> =
        when (val result = shows.findNearest(movieId?.let(::MovieId), limit)) {
            is Succ<List<ShowEntity>> -> ResponseEntity.ok(result.value.map { it.toShow() })
            is Err<ShowsError> -> result.asErrorResponse(::errorDetails)
        }

    data class AddShowReq(val movieId: UUID, val startTime: Instant, val ticketPrice: Api.Money)
    data class UpdateShowReq(val startTime: Instant, val ticketPrice: Api.Money)
    data class ShowRes(val id: UUID, val startTime: Instant)

    companion object {
        private fun AddShowReq.toEntity(staffUserId: StaffUserId) =
            ShowEntity(
                movieId = MovieId(movieId),
                startTime = startTime,
                ticketPrice = ticketPrice.toMoney(),
                createdBy = staffUserId,
            )

        private fun ShowEntity.toShow() =
            ShowRes(id!!.value, startTime)

        private fun errorDetails(err: ShowsError): Pair<Int, String> =
            when (err) {
                is Shows.Errors.BadInput -> 400 to err.details
                is Shows.Errors.ShowNotFound -> 404 to "Show with id: ${err.id} not found"
            }
    }
}