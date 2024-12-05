package pl.fnfcinema.cinema.movies

interface MovieDetailsProvider {

    fun fetchDetails(id: String): MovieDetails

}

