package pl.fnfcinema.cinema

import java.math.BigDecimal
import java.util.Currency

data class Money(val amount: BigDecimal, val currency: Currency) {

    constructor(amount: Int) : this(BigDecimal(amount), PLN)

    companion object {
        val PLN = Currency.getInstance("PLN")
    }
}
