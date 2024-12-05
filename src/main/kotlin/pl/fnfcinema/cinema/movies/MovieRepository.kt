package pl.fnfcinema.cinema.movies

import org.springframework.data.repository.CrudRepository
import java.util.*

interface MovieRepository : CrudRepository<MovieEntity, UUID>
