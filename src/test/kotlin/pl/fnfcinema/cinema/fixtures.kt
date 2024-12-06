package pl.fnfcinema.cinema

import io.github.serpro69.kfaker.Faker

val faker = Faker()

fun <R, E> Res<R, E>.requireSucc(): R = when (this) {
    is Succ<R> -> value
    is Err<E> -> throw IllegalStateException()
}