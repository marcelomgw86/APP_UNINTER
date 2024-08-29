package com.example.xara

import MovieViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.a4099000.R
import okhttp3.Response

class MainActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter
    private val apiKey = "0e8461b00881864025075aca80e25236"

    private val detailActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedMovie = result.data?.getSerializableExtra("updated_movie") as? Movie
            updatedMovie?.let {
                movieAdapter.updateMovie(it)
                movieViewModel.updateMovie(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MovieAdapter(
            clickListener = { movie ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("movie", movie)
                }
                detailActivityLauncher.launch(intent)
            },
            favoriteClickListener = { movie ->
                if (movie.isFavorite) {
                    movieViewModel.removeFavorite(movie)
                } else {
                    movieViewModel.addFavorite(movie)
                }
            }
        )

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        observeViewModel()

        if (isOnline()) {
            fetchMovies()
        } else {
            loadFavoriteMovies()
        }
    }

    private fun observeViewModel() {
        movieViewModel.favoriteMovies.observe(this, Observer { favoriteMovies ->
            combineAndDisplayMovies(movieViewModel.allMovies.value.orEmpty(), favoriteMovies)
        })

        movieViewModel.allMovies.observe(this, Observer { allMovies ->
            combineAndDisplayMovies(allMovies, movieViewModel.favoriteMovies.value.orEmpty())
        })
    }

    private fun fetchMovies() {
        // Assume a Retrofit call similar to the previous example to fetch movies
        RetrofitInstance.api.getPopularMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { movieResponse ->
                        movieViewModel.setAllMovies(movieResponse.results)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch movies", Toast.LENGTH_SHORT).show()
                loadFavoriteMovies()
            }
        })
    }

    private fun loadFavoriteMovies() {
        // This method is already called when observing the favoriteMovies LiveData
    }

    private fun combineAndDisplayMovies(allMovies: List<Movie>, favoriteMovies: List<Movie>) {
        val combinedMovies = favoriteMovies + allMovies.filter { it !in favoriteMovies }
        movieAdapter.setMovies(combinedMovies)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
