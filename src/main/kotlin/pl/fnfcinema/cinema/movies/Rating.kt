package pl.fnfcinema.cinema.movies

import java.math.BigDecimal

data class Rating(val votes: Long, val total: Long) {

    constructor() : this(0, 0)

    fun avg(): BigDecimal? = when (votes) {
        0L -> null
        else -> total.toBigDecimal().setScale(1).div(votes.toBigDecimal())
    }

    fun record(rate: Rate): Rating = copy(votes = votes + 1, total = total + rate.value)

}