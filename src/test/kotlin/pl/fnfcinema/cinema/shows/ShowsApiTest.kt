package pl.fnfcinema.cinema.shows

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.Security.X_STAFF_USER_ID_HEADER
import pl.fnfcinema.cinema.BaseApiTest
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.aFutureStartTime
import pl.fnfcinema.cinema.aShow
import pl.fnfcinema.cinema.aShowDetails
import pl.fnfcinema.cinema.aStaffUserId
import pl.fnfcinema.cinema.aTicketPrice
import pl.fnfcinema.cinema.anAddShowReq
import pl.fnfcinema.cinema.anUpdateShowReq
import pl.fnfcinema.cinema.movies.MovieId
import pl.fnfcinema.cinema.shows.Shows.Errors.BadInput
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowNotFound
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class ShowsApiTest : BaseApiTest() {

    @Test
    fun should_add_new_show() {
        // given
        val staffUserId = aStaffUserId()
        val addShowReq = ShowsApi.AddShowReq(
            movieId = UUID.randomUUID(),
            startTime = Instant.now(),
            ticketPrice = Api.Money(20.00.toBigDecimal(), "PLN")
        )

        val newShowEntity = ShowEntity(
            movieId = MovieId(addShowReq.movieId),
            startTime = addShowReq.startTime,
            ticketPrice = Money(20.00.toBigDecimal()),
            createdBy = staffUserId
        )

        val createdShowEntityId = ShowId(UUID.randomUUID())
        every { shows.addShow(any()) } returns Succ(newShowEntity.copy(id = createdShowEntityId))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, staffUserId.id)
                .content(json.writeValueAsBytes(addShowReq))
        ).andReturn().response

        // then
        verify { shows.addShow(newShowEntity) }

        assertEquals(201, response.status)
        assertEquals(
            ShowsApi.BasicShowRes(createdShowEntityId.value, addShowReq.startTime, addShowReq.ticketPrice),
            json.parse(response)
        )
    }

    @Test
    fun `should secure add show endpoint and require valid staff-user-id`() {
        // given
        val newShow = anAddShowReq()

        listOf(
            post("/shows")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, "not-valid-user-id")
                .content(json.writeValueAsBytes(newShow)),
            post("/shows")
                .contentType(APPLICATION_JSON)
                .content(json.writeValueAsBytes(newShow)),
        ).forEach { invalidReq ->
            // when
            val response = mockMvc.perform(invalidReq).andReturn().response

            // then
            assertEquals(403, response.status)
        }
    }

    @Test
    fun `should delete show`() {
        // given
        val showId = ShowId(UUID.randomUUID())

        every { shows.deleteShow(ShowId(any())) } returns Succ(Unit)

        // when
        val response = mockMvc.perform(
            delete("/shows/${showId.value}")
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
        ).andReturn().response

        // then
        verify { shows.deleteShow(showId) }
        assertEquals(response.status, 204)
    }

    @Test
    fun `should secure delete show endpoint and require valid staff-user-id`() {
        // given
        val showId = UUID.randomUUID()

        listOf(
            delete("/shows/$showId")
                .header(X_STAFF_USER_ID_HEADER, "not-valid-user-id"),
            delete("/shows/$showId"),
        ).forEach { invalidReq ->
            // when
            val response = mockMvc.perform(invalidReq).andReturn().response

            // then
            assertEquals(403, response.status)
        }
    }

    @Test
    fun `should respond http 404 for unknown show deletion`() {
        // given
        val unknownShowId = ShowId(UUID.randomUUID())

        every { shows.deleteShow(ShowId(any())) } returns Err(ShowNotFound(unknownShowId))

        // when
        val response = mockMvc.perform(
            delete("/shows/${unknownShowId.value}")
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
        ).andReturn().response

        // then
        verify { shows.deleteShow(unknownShowId) }
        assertEquals(404, response.status)
        assertEquals(
            Api.ErrorDetails("Show with id: $unknownShowId not found"),
            json.parse(response)
        )
    }

    @Test
    fun `should update show`() {
        // given
        val showId = ShowId(UUID.randomUUID())
        val show = aShow(id = showId)
        val updatedShow = show.copy(
            startTime = show.startTime + 1.days.toJavaDuration(),
            ticketPrice = show.ticketPrice * 2
        )

        every { shows.updateShow(ShowId(any()), any(), any()) } returns Succ(updatedShow)

        // when
        val response = mockMvc.perform(
            put("/shows/${showId.value}")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
                .content(
                    json.writeValueAsBytes(
                        ShowsApi.UpdateShowReq(
                            startTime = updatedShow.startTime,
                            ticketPrice = Api.Money(updatedShow.ticketPrice)
                        )
                    )
                )
        ).andReturn().response

        // then
        verify { shows.updateShow(show.id!!, updatedShow.startTime, updatedShow.ticketPrice) }
        assertEquals(200, response.status)
    }

    @Test
    fun `should respond http 404 for unknown show update`() {
        // given
        val unknownShowId = ShowId(UUID.randomUUID())
        val updateReq = ShowsApi.UpdateShowReq(
            aFutureStartTime(),
            Api.Money(aTicketPrice())
        )
        every { shows.updateShow(ShowId(any()), any(), any()) } returns Err(ShowNotFound(unknownShowId))

        // when
        val response = mockMvc.perform(
            put("/shows/${unknownShowId.value}")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
                .content(json.writeValueAsBytes(updateReq))
        ).andReturn().response

        // then
        verify { shows.updateShow(unknownShowId, updateReq.startTime, updateReq.ticketPrice.toMoney()) }

        assertEquals(404, response.status)
        assertEquals(
            Api.ErrorDetails("Show with id: $unknownShowId not found"),
            json.parse(response)
        )
    }

    @Test
    fun `should secure update show endpoint and require valid staff-user-id`() {
        // given
        val showId = UUID.randomUUID()
        val showUpdate = anUpdateShowReq()

        listOf(
            put("/shows/${showId}")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, "invalid-staff-user-id")
                .content(json.writeValueAsBytes(showUpdate)),
            put("/shows/${showId}")
                .contentType(APPLICATION_JSON)
                .content(json.writeValueAsBytes(showUpdate)),
        ).forEach { invalidReq ->
            // when
            val response = mockMvc.perform(invalidReq).andReturn().response

            // then
            assertEquals(403, response.status)
        }
    }

    @Test
    fun should_respond_http400_for_invalid_data() {
        // given
        val staffUserId = aStaffUserId()
        val addShowReq = ShowsApi.AddShowReq(UUID.randomUUID(), Instant.now(), Api.Money(aTicketPrice()))

        every { shows.addShow(any()) } returns Err(BadInput("some error"))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, staffUserId.id)
                .content(json.writeValueAsBytes(addShowReq))
        ).andReturn().response

        // then
        assertEquals(400, response.status)
        verify {
            shows.addShow(
                ShowEntity(
                    MovieId(addShowReq.movieId),
                    addShowReq.startTime,
                    addShowReq.ticketPrice.toMoney(),
                    staffUserId
                )
            )
        }
    }

    @Test
    fun `should list all available shows without movie filter`() {
        // given
        val limit = 15
        val availableShowDetails = (1..5).map { aShowDetails() }
        every { shows.findNearest(MovieId(any()), any()) } returns Succ(availableShowDetails)

        // when
        val response = mockMvc.perform(
            get("/shows")
                .queryParam("limit", limit.toString())
        ).andReturn().response

        // then
        verify { shows.findNearest(null, limit) }

        assertEquals(200, response.status)
        assertEquals(
            availableShowDetails.map(::toShowRes),
            json.parseList(response)
        )
    }

    @Test
    fun `should list all available shows with movie filter`() {
        // given
        val limit = 15
        val movieId = UUID.randomUUID()
        val availableShowDetails = (1..5).map { aShowDetails() }
        every { shows.findNearest(MovieId(any()), any()) } returns Succ(availableShowDetails)

        // when
        val response = mockMvc.perform(
            get("/shows")
                .queryParam("limit", limit.toString())
                .queryParam("movie", movieId.toString())
        ).andReturn().response

        // then
        verify { shows.findNearest(MovieId(movieId), limit) }

        assertEquals(200, response.status)
        val expected: List<ShowsApi.ShowRes> = availableShowDetails.map(::toShowRes)
        val actual: List<ShowsApi.ShowRes> = json.parseList<ShowsApi.ShowRes>(response)
        assertEquals(
            expected,
            actual
        )
    }

    private fun toShowRes(details: ShowDetails) = ShowsApi.ShowRes(
        id = details.id.value,
        startTime = details.startTime,
        ticketPrice = Api.Money(details.ticketPrice),
        movieId = details.movieId.value,
        movieTitle = details.movieTitle,
    )
}
