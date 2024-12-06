package pl.fnfcinema.cinema

import org.springframework.http.ResponseEntity

object Api {

    fun <T> entityOrNotFound(entity: T?): ResponseEntity<T> =
        entity?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    fun <T, E> Err<E>.asErrorResponse(errorMapper: (E) -> Pair<Int, String>): ResponseEntity<T> {
        val (statsCode, details) = errorMapper(error)
        @Suppress("UNCHECKED_CAST")
        return ResponseEntity.status(statsCode).body(ErrorDetails(details)) as ResponseEntity<T>
    }

    data class ErrorDetails(val message: String)

}