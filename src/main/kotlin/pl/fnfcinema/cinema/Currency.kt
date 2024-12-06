package pl.fnfcinema.cinema

import java.util.Currency

data class Currency(val code: String) {
    init {
        require(Currency.getInstance(code) != null, { "Invalid currency: $code" })
    }

    companion object {
        val PLN = Currency("PLN")
    }
}
