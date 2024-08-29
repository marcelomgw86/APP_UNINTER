package com.example.xara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a4099000.R
import com.squareup.picasso.Picasso

class MovieAdapter(private val clickListener: (Movie) -> Unit, private val favoriteClickListener: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies = mutableListOf<Movie>()

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val movieOverview: TextView = itemView.findViewById(R.id.movieOverview)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)

        fun bind(movie: Movie) {
            movieTitle.text = movie.title
            movieOverview.text = movie.overview
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + movie.poster_path).into(moviePoster)
            favoriteIcon.setImageResource(if (movie.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

            itemView.setOnClickListener { clickListener(movie) }
            favoriteIcon.setOnClickListener {
                favoriteClickListener(movie)
                movie.isFavorite = !movie.isFavorite  // Toggle the favorite status locally
                favoriteIcon.setImageResource(if (movie.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun setMovies(movies: List<Movie>) {
        this.movies = movies.toMutableList()
        notifyDataSetChanged()
    }

    fun updateMovie(updatedMovie: Movie) {
        val index = movies.indexOfFirst { it.id == updatedMovie.id }
        if (index != -1) {
            movies[index] = updatedMovie
            notifyItemChanged(index)
        }
    }
}
