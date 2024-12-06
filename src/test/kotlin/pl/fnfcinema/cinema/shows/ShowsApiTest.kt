package pl.fnfcinema.cinema.shows

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import pl.fnfcinema.cinema.Api
import pl.fnfcinema.cinema.Api.Security.X_STAFF_USER_ID_HEADER
import pl.fnfcinema.cinema.ApiTest
import pl.fnfcinema.cinema.Err
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Succ
import pl.fnfcinema.cinema.aNewShow
import pl.fnfcinema.cinema.aShow
import pl.fnfcinema.cinema.aShowUpdate
import pl.fnfcinema.cinema.aStaffUserId
import pl.fnfcinema.cinema.aTicketPrice
import pl.fnfcinema.cinema.shows.Shows.Errors.BadInput
import pl.fnfcinema.cinema.shows.Shows.Errors.ShowNotFound
import pl.fnfcinema.cinema.shows.ShowsApi.Requests
import pl.fnfcinema.cinema.shows.ShowsApi.Responses
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class ShowsApiTest : ApiTest() {

    @Test
    fun should_add_new_show() {
        // given
        val staffUserId = aStaffUserId()
        val newShow = Requests.NewShow(
            movieId = UUID.randomUUID(),
            startTime = Instant.now(),
            ticketPrice = Api.Money(20.00.toBigDecimal(), "PLN")
        )

        val newShowEntity = ShowEntity(
            movieId = AggregateReference.to(newShow.movieId),
            startTime = newShow.startTime,
            ticketPrice = Money(20.00.toBigDecimal()),
            createdBy = staffUserId
        )

        val createdShowEntityId = UUID.randomUUID()
        every { shows.addShow(any()) } returns Succ(newShowEntity.copy(id = createdShowEntityId))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, staffUserId.id)
                .content(json.writeValueAsBytes(newShow))
        ).andReturn().response

        // then
        assertEquals(201, response.status)

        verify { shows.addShow(newShowEntity) }

        assertEquals(
            Responses.Show(createdShowEntityId, newShow.startTime),
            json.parse(response)
        )
    }

    @Test
    fun `should secure add show endpoint and require valid staff-user-id`() {
        // given
        val newShow = aNewShow()

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
        val showId = UUID.randomUUID()

        every { shows.deleteShow(any()) } returns Succ(Unit)

        // when
        val response = mockMvc.perform(
            delete("/shows/${showId}")
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
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, "not-valid-user-id"),
            delete("/shows/$showId")
                .contentType(APPLICATION_JSON),
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
        val unknownShowId = UUID.randomUUID()

        every { shows.deleteShow(any()) } returns Err(ShowNotFound(unknownShowId))

        // when
        val response = mockMvc.perform(
            delete("/shows/$unknownShowId")
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
        val show = aShow(id = UUID.randomUUID())
        val updatedShow = show.copy(
            startTime = show.startTime + 1.days.toJavaDuration(),
            ticketPrice = show.ticketPrice * 2
        )

        every { shows.updateShow(any(), any(), any()) } returns Succ(updatedShow)

        // when
        val response = mockMvc.perform(
            put("/shows/${show.id}")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, aStaffUserId().id)
                .content(
                    json.writeValueAsBytes(
                        Requests.ShowUpdate(
                            startTime = updatedShow.startTime,
                            ticketPrice = Api.Money(updatedShow.ticketPrice)
                        )
                    )
                )
        ).andReturn().response

        // then
        assertEquals(200, response.status)
        verify { shows.updateShow(show.id!!, updatedShow.startTime, updatedShow.ticketPrice) }
    }

    @Test
    fun `should respond http 404 for unknown show update`() {
        // given
        val unknownShowId = UUID.randomUUID()
        val updateReq = Requests.ShowUpdate(
            Instant.now() + 3.days.toJavaDuration(),
            Api.Money(aTicketPrice())
        )
        every { shows.updateShow(any(), any(), any()) } returns Err(ShowNotFound(unknownShowId))

        // when
        val response = mockMvc.perform(
            put("/shows/${unknownShowId}")
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
        val showUpdate = aShowUpdate()

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
        val newShow = Requests.NewShow(UUID.randomUUID(), Instant.now(), Api.Money(aTicketPrice()))

        every { shows.addShow(any()) } returns Err(BadInput("some error"))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .contentType(APPLICATION_JSON)
                .header(X_STAFF_USER_ID_HEADER, staffUserId.id)
                .content(json.writeValueAsBytes(newShow))
        ).andReturn().response

        // then
        assertEquals(400, response.status)
        verify {
            shows.addShow(
                ShowEntity(
                    AggregateReference.to(newShow.movieId),
                    newShow.startTime,
                    newShow.ticketPrice.toMoney(),
                    staffUserId
                )
            )
        }
    }
}