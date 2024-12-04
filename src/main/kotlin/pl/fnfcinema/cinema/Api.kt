package pl.fnfcinema.cinema

import org.springframework.http.ResponseEntity

object Api {

    fun <T> entityOrNotFound(entity: T?): ResponseEntity<T> =
        entity?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

}