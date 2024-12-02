package pl.fnfcinema.cinema

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class CinemaApplication(
) : CommandLineRunner {

    override fun run(vararg args: String?) {
    }
}

fun main(args: Array<String>) {
    runApplication<CinemaApplication>(*args)
}
