package pl.fnfcinema.cinema.movies


data class Rate(val value: Int) {

    init {
        require(valueRange.contains(value), { "Value: $value is not in range: $valueRange" })
    }

    companion object {
        const val SCALE = 5
        val valueRange: IntRange = 1..SCALE

        fun fromInt(value: Int): Rate? = if (valueRange.contains(value)) Rate(value) else null
    }
}
