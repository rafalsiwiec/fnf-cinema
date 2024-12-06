package pl.fnfcinema.cinema

import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pl.fnfcinema.cinema.OptimisticLocking.ConflictError

class ApiConfig {

    @RestControllerAdvice
    class ConflictErrorMapper {

        @ExceptionHandler(ConflictError::class)
        fun handleConflictError(error: ConflictError): ResponseEntity<Unit> = ResponseEntity.status(CONFLICT).build()

    }

}