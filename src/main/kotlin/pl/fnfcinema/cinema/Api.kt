package pl.fnfcinema.cinema

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.APIKEY
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import pl.fnfcinema.cinema.Api.Security.STAFF_ONLY
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

@OpenAPIDefinition(
    info = Info(
        title = "Fast and Furious Cinema",
        description = "API behind Fast and Furious Cinema app"
    )
)
object Api {

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

    @SecurityScheme(
        type = APIKEY,
        name = STAFF_ONLY,
        paramName = "X-Staff-User-Id",
        `in` = HEADER,
        description = "Valid UUID identifier of StaffUser"
    )
    object Security {

        const val STAFF_ONLY = "staff-only"

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