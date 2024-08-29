package com.example.xara

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import com.example.a4099000.R
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieve movie data from intent
        movie = intent.getSerializableExtra("movie") as Movie

        // Initialize views
        val moviePoster = findViewById<ImageView>(R.id.moviePoster)
        val movieTitle = findViewById<TextView>(R.id.movieTitle)
        val movieDescription = findViewById<TextView>(R.id.movieDescription)
        val movieSecondImage = findViewById<ImageView>(R.id.movieSecondImage)
        val favoriteButton = findViewById<Button>(R.id.favoriteButton)
        val favoriteIcon = findViewById<ImageView>(R.id.favoriteIcon)

        // Set movie details
        movieTitle.text = movie.title
        movieDescription.text = movie.overview
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + movie.poster_path).into(moviePoster)

        // Set favorite icon based on favorite status
        favoriteIcon.setImageResource(if (movie.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

        // Initialize ViewModel
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        // Set click listener for favorite button
        favoriteButton.setOnClickListener {
            movie.isFavorite = !movie.isFavorite
            if (movie.isFavorite) {
                movieViewModel.addFavorite(movie)
            } else {
                movieViewModel.removeFavorite(movie)
            }
            // Update favorite icon
            favoriteIcon.setImageResource(if (movie.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent().apply {
            putExtra("updated_movie", movie)
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}
