package pl.fnfcinema.cinema

import io.github.serpro69.kfaker.Faker
import java.util.UUID

val faker = Faker()

fun aStaffUserId() = StaffUserId(UUID.randomUUID())

fun <R, E> Res<R, E>.requireSucc(): R = when (this) {
    is Succ<R> -> value
    is Err<E> -> throw IllegalStateException()
}