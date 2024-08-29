package com.example.xara

import androidx.lifecycle.LiveData

class MovieRepository(private val movieDao: MovieDao) {

    val favoriteMovies: LiveData<List<Movie>> = movieDao.getFavoriteMovies()

    suspend fun addFavorite(movie: Movie) {
        movieDao.addFavorite(movie)
    }

    suspend fun removeFavorite(movie: Movie) {
        movieDao.removeFavorite(movie)
    }
}
