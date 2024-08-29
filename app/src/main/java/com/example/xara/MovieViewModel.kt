package com.example.xara

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository: MovieRepository

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> = _favoriteMovies

    private val _allMovies = MutableLiveData<List<Movie>>()
    val allMovies: LiveData<List<Movie>> = _allMovies

    private val favoriteMovieList = mutableListOf<Movie>()

    init {
        val movieDao = MovieDatabase.getDatabase(application).movieDao()
        movieRepository = MovieRepository(movieDao)
        loadFavoriteMoviesFromDb()
    }

    fun addFavorite(movie: Movie) {
        if (!favoriteMovieList.contains(movie)) {
            favoriteMovieList.add(movie)
            _favoriteMovies.value = favoriteMovieList
            viewModelScope.launch {
                movieRepository.addFavorite(movie)
            }
        }
    }

    fun removeFavorite(movie: Movie) {
        if (favoriteMovieList.contains(movie)) {
            favoriteMovieList.remove(movie)
            _favoriteMovies.value = favoriteMovieList
            viewModelScope.launch {
                movieRepository.removeFavorite(movie)
            }
        }
    }

    fun updateMovie(updatedMovie: Movie) {
        val index = favoriteMovieList.indexOfFirst { it.id == updatedMovie.id }
        if (index != -1) {
            favoriteMovieList[index] = updatedMovie
            _favoriteMovies.value = favoriteMovieList
            viewModelScope.launch {
                movieRepository.addFavorite(updatedMovie)  // To update the movie in the database
            }
        }
    }

    fun loadFavoriteMoviesFromDb() {
        movieRepository.favoriteMovies.observeForever { movies ->
            favoriteMovieList.clear()
            favoriteMovieList.addAll(movies)
            _favoriteMovies.value = favoriteMovieList
        }
    }

    fun setAllMovies(movies: List<Movie>) {
        _allMovies.value = movies
    }
}
