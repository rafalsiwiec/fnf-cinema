package pl.fnfcinema.cinema

import io.github.serpro69.kfaker.Faker
import java.time.LocalDate
import java.util.*

val faker = Faker()

fun aStaffUserId() = StaffUserId(UUID.randomUUID())

fun aPastDate() = LocalDate.of(
    faker.random.nextInt(1960, 2023),
    faker.random.nextInt(1, 12),
    faker.random.nextInt(1, 28)
)

fun <R, E> Res<R, E>.requireSucc(): R = when (this) {
    is Succ<R> -> value
    is Err<E> -> throw IllegalStateException()
}