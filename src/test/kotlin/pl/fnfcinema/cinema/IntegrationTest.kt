package pl.fnfcinema.cinema

import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import pl.fnfcinema.cinema.integrations.imdb.ImdbApi

@SpringBootTest
@Import(IntegrationTest.TestConfig::class)
abstract class IntegrationTest {

    @MockkBean
    lateinit var imdbApi: ImdbApi

    @Autowired
    lateinit var mutableClock: MutableClock

    @Autowired
    lateinit var repos: List<CrudRepository<*, *>>

    @BeforeEach
    fun setUp() {
        repos.forEach { it.deleteAll() }
    }

    @AfterEach
    fun tearDown() {
        confirmVerified(imdbApi)
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        @Primary
        fun mutableClock(): MutableClock = MutableClock()
    }
}
