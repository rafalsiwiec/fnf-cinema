package pl.fnfcinema.cinema.movies

data class Rate(val value: Int) {

    init {
        require(valueRange.contains(value), { "Value: $value is not in range: $valueRange" })
    }

    companion object {
        val valueRange: IntRange = 1..5

        fun create(value: Int): Rate? = if (valueRange.contains(value)) Rate(value) else null
    }
}
