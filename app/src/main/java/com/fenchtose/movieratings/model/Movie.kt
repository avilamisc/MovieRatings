package com.fenchtose.movieratings.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.fenchtose.movieratings.model.db.MovieTypeConverter2
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "MOVIES")
@TypeConverters(value = MovieTypeConverter2::class)
class Movie {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "TITLE")
    @SerializedName("Title")
    var title: String = ""

    @ColumnInfo(name = "POSTER")
    @SerializedName("Poster")
    var poster: String = ""

    @ColumnInfo(name = "RATINGS")
    @SerializedName("Ratings")
    var ratings: ArrayList<Rating> = ArrayList()

    @ColumnInfo(name = "TYPE")
    @SerializedName("Type")
    var type: String = ""

    @ColumnInfo(name = "IMDBID")
    @SerializedName("imdbID")
    var imdbId: String = ""

    @ColumnInfo(name = "YEAR")
    @SerializedName("Year")
    var year: String = ""

    @ColumnInfo(name = "RATED")
    @SerializedName("Rated")
    var rated: String = ""

    @ColumnInfo(name = "RELEASED")
    @SerializedName("Released")
    var released: Date = Date()

    @ColumnInfo(name = "RUNTIME")
    @SerializedName("Runtime")
    var runtime: String = ""

    @ColumnInfo(name = "GENRE")
    @SerializedName("Genre")
    var genre: String = ""

    @ColumnInfo(name = "DIRECTOR")
    @SerializedName("Director")
    var director: String = ""

    @ColumnInfo(name = "WRITERS")
    @SerializedName("Writers")
    var writers: String = ""

    @ColumnInfo(name = "ACTORS")
    @SerializedName("Actors")
    var actors: String = ""

    @ColumnInfo(name = "PLOT")
    @SerializedName("Plot")
    var plot: String = ""

    @ColumnInfo(name = "LANGUAGE")
    @SerializedName("Language")
    var language: String = ""

    @ColumnInfo(name = "COUNTRY")
    @SerializedName("Country")
    var country: String = ""

    @ColumnInfo(name = "AWARDS")
    @SerializedName("Awards")
    var awards: String = ""

    @ColumnInfo(name = "IMDBVOTES")
    @SerializedName("imdbVotes")
    var imdbVotes: String = ""

    @ColumnInfo(name = "PRODUCTION")
    @SerializedName("Production")
    var production: String = ""

    @ColumnInfo(name = "WEBSITE")
    @SerializedName("Website")
    var website: String = ""

    override fun toString(): String {
        return "Movie(id='$id', title='$title', poster='$poster', ratings=$ratings)"
    }

    companion object {
        fun empty() : Movie {
            val movie = Movie()
            movie.id = -1
            return movie
        }
    }
}