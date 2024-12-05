package pl.fnfcinema.cinema.shows

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import pl.fnfcinema.cinema.ApiTest
import pl.fnfcinema.cinema.Error
import pl.fnfcinema.cinema.Money
import pl.fnfcinema.cinema.Success
import pl.fnfcinema.cinema.shows.Shows.Errors.InvalidData
import pl.fnfcinema.cinema.shows.ShowsApi.Requests
import pl.fnfcinema.cinema.shows.ShowsApi.Responses
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class ShowsApiTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) : ApiTest() {

    @Test
    fun should_add_new_show() {
        // given
        val newShow = Requests.NewShow(
            movieId = UUID.randomUUID(),
            startTime = Instant.now(),
            price = Money(20_00)
        )

        val newShowEntity = ShowEntity(
            AggregateReference.to(newShow.movieId),
            newShow.startTime,
            Money(20_00)
        )

        val createdShowEntityId = UUID.randomUUID()
        every { shows.addShow(any()) } returns Success(newShowEntity.copy(id = createdShowEntityId))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .header("content-type", "application/json")
                .content(objectMapper.writeValueAsBytes(newShow))
        ).andReturn().response

        // then
        assertEquals(201, response.status)

        verify { shows.addShow(newShowEntity) }

        val responseBody = objectMapper.readValue(response.contentAsByteArray, Responses.Show::class.java)
        assertEquals(
            Responses.Show(createdShowEntityId, newShow.startTime),
            responseBody
        )
    }

    @Test
    fun should_respond_http400_for_invalid_data() {
        // given
        val newShow = Requests.NewShow(UUID.randomUUID(), Instant.now(), Money(20_00))

        every { shows.addShow(any()) } returns Error(InvalidData("some error"))

        // when
        val response = mockMvc.perform(
            post("/shows")
                .header("content-type", "application/json")
                .content(objectMapper.writeValueAsBytes(newShow))
        ).andReturn().response

        // then
        assertEquals(400, response.status)
        verify {
            shows.addShow(
                ShowEntity(
                    AggregateReference.to(newShow.movieId),
                    newShow.startTime,
                    Money(20_00)
                )
            )
        }
    }
}