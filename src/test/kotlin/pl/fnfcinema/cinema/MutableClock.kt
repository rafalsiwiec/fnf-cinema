package pl.fnfcinema.cinema

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class MutableClock(private val clock: Clock = systemUTC()) : Clock() {

    private var fixedTime: Instant? = null

    override fun instant(): Instant = fixedTime ?: clock.instant()

    override fun withZone(zone: ZoneId?): Clock = throw NotImplementedError()

    override fun getZone(): ZoneId = throw NotImplementedError()

    fun stopTime(time: Instant = clock.instant()): Instant {
        fixedTime = time
        return time
    }

    fun addTime(duration: Duration) {
        fixedTime = fixedTime!!.plus(duration.toJavaDuration())
    }

    fun reset() {
        fixedTime = null
    }
}