package pl.fnfcinema.cinema

import java.math.BigDecimal

data class Money(val amount: BigDecimal, val currency: Currency = Currency.PLN) {

    operator fun times(i: Int): Money = copy(amount = amount.times(BigDecimal(i)))

}
