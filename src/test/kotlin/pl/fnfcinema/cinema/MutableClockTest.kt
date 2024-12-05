package pl.fnfcinema.cinema

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class MutableClockTest {

    @Test
    fun `should fix time`() {
        // given
        val clock = MutableClock()

        // when
        val stopTime = clock.stopTime()

        // then
        assertEquals(stopTime, clock.instant())
    }

    @Test
    fun `should fix time at provided moment`() {
        // given
        val clock = MutableClock()
        val ts = Instant.parse("2021-11-12T12:00:00.000Z")

        // when
        val stopTime = clock.stopTime(ts)

        // then
        assertEquals(ts, stopTime)
        assertEquals(ts, clock.instant())
    }

    @Test
    fun `should shift time`() {
        // given
        val clock = MutableClock()
        val time = clock.stopTime()

        // when
        clock.addTime(1.hours)

        // then
        assertEquals(time + 1.hours.toJavaDuration(), clock.instant())
    }
}