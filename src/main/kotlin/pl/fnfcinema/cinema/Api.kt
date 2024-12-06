package pl.fnfcinema.cinema

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

object Api {

    fun <T> entityOrNotFound(entity: T?): ResponseEntity<T> =
        entity?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    fun <T, E> Err<E>.asErrorResponse(errorMapper: (E) -> Pair<Int, String>): ResponseEntity<T> {
        val (statsCode, details) = errorMapper(error)
        @Suppress("UNCHECKED_CAST")
        return ResponseEntity.status(statsCode).body(ErrorDetails(details)) as ResponseEntity<T>
    }

    data class Money(val amount: BigDecimal, val currency: String) {
        constructor(money: pl.fnfcinema.cinema.Money) : this(money.amount, money.currency.code)

        fun toMoney(): pl.fnfcinema.cinema.Money = Money(amount, Currency(currency))
    }

    data class ErrorDetails(val message: String)

    object Security {

        private val staffUserId: ThreadLocal<StaffUserId> = ThreadLocal()

        private fun setStaffUserId(staffUserId: StaffUserId) {
            this.staffUserId.set(staffUserId)
        }

        private fun clearStaffUserId() {
            staffUserId.set(null)
        }

        const val X_STAFF_USER_ID_HEADER = "X-Staff-User-Id"

        fun requireStaffUserId(): StaffUserId = staffUserId.get() ?: throw NoUserIdentityError()

        class NoUserIdentityError : RuntimeException()

        @Component
        class StaffUserIdPopulatingFilter : Filter {

            override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
                try {
                    if (req is HttpServletRequest) {
                        req.getHeader(X_STAFF_USER_ID_HEADER)?.let { xStaffUserIdHeader ->
                            try {
                                setStaffUserId(StaffUserId(UUID.fromString(xStaffUserIdHeader)))
                            } catch (_: Exception) {
                                logger.error { "Unable to parse staff-user-id from: $xStaffUserIdHeader" }
                            }
                        }
                    }
                    chain.doFilter(req, res)
                } finally {
                    clearStaffUserId()
                }
            }
        }

    }

}