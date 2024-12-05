package pl.fnfcinema.cinema

abstract class BaseTest {

    companion object {
        fun <R, E> Result<R, E>.get(): R = when(this) {
            is Success<R> -> value
            is Error<E> -> throw IllegalStateException()
        }
    }

}