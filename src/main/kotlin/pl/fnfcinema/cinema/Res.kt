package pl.fnfcinema.cinema

sealed interface Res<out R, out E>

data class Succ<R>(val value: R) : Res<R, Nothing>
data class Err<E>(val error: E) : Res<Nothing, E>
