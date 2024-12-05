package pl.fnfcinema.cinema

import java.math.BigDecimal
import java.util.*

data class Money(val amount: BigDecimal, val currency: Currency = PLN) {
    companion object {
        val PLN: Currency = Currency.getInstance("PLN")
    }
}
