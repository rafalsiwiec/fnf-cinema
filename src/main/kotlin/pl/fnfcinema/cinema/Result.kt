package pl.fnfcinema.cinema

sealed interface Result<out R, out E>

data class Success<R>(val value: R) : Result<R, Nothing>
data class Error<E>(val error: E) : Result<Nothing, E>
