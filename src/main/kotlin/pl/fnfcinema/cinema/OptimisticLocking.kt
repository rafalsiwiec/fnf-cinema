package pl.fnfcinema.cinema

import org.springframework.dao.OptimisticLockingFailureException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object OptimisticLocking {

    private val defaultRetryPeriod = 1.seconds

    fun <T> rerunOnConflict(withinMax: Duration = defaultRetryPeriod, code: () -> T): T {
        val timeout = withinMax.inWholeMilliseconds
        val start = System.currentTimeMillis()
        var latestError: OptimisticLockingFailureException?
        do {
            try {
                return code()
            } catch (err: OptimisticLockingFailureException) {
                latestError = err
            }
        } while (System.currentTimeMillis() - start < timeout)
        throw ConflictError(latestError)
    }

    class ConflictError(cause: OptimisticLockingFailureException?) : RuntimeException(cause)
}