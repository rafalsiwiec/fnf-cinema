package pl.fnfcinema.cinema.movies

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RatingTest {

    @Test
    fun `should return null average rate when no rates were recorded`() {
        assertNull(Rating().avg())
    }

    @Test
    fun `should record ratings and calculate average based on them`() {
        val rating = Rating()

        val update1 = rating.record(Rate(5))

        assertEquals(1, update1.votes)
        assertEquals(5, update1.total)
        assertEquals(BigDecimal("5.0"), update1.avg())

        val update2 = update1.record(Rate(2))

        assertEquals(2, update2.votes)
        assertEquals(7, update2.total)
        assertEquals(BigDecimal("3.5"), update2.avg())

        val update3 = update2.record(Rate(1))
        assertEquals(3, update3.votes)
        assertEquals(8, update3.total)
        assertEquals(BigDecimal("2.7"), update3.avg())
    }
}