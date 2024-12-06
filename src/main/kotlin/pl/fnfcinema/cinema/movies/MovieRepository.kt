package pl.fnfcinema.cinema.movies

import org.springframework.data.repository.CrudRepository

interface MovieRepository : CrudRepository<MovieEntity, MovieId>
