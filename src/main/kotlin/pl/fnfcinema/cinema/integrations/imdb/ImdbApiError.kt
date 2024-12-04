package pl.fnfcinema.cinema.integrations.imdb

class ImdbApiError(message: String?, cause: Exception?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
    constructor(cause: Exception) : this(null, cause)
}