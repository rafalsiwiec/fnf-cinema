package pl.fnfcinema.cinema

import org.springframework.dao.OptimisticLockingFailureException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object OptimisticLocking {

    private val defaultRetryTimeout = 1.seconds

    fun <T> rerunOnConflict(withinAtMax: Duration = defaultRetryTimeout, code: () -> T): T {
        val timeout = withinAtMax.inWholeMilliseconds
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

    class ConflictError(cause: OptimisticLockingFailureException? = null) : RuntimeException(cause)
}