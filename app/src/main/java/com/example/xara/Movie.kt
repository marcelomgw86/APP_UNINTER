package com.example.xara

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    var isFavorite: Boolean = false
) : Serializable
