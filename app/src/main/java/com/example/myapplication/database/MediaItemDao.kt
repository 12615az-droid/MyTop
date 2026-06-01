package com.example.myapplication.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ============================================
// БАЗОВЫЙ DAO (общие операции для всех)
// ============================================
interface BaseDao<T> {
    @Upsert
    suspend fun upsert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<T>)

    @Delete
    suspend fun delete(item: T)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Int)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Int)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: Int)

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteSeriesById(id: Int)

    @Query("DELETE FROM music WHERE id = :id")
    suspend fun deleteMusicById(id: Int)
}

// ============================================
// ИГРЫ
// ============================================
@Dao
interface GameDao {
    @Upsert
    suspend fun upsert(item: Game): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Game>)

    @Delete
    suspend fun delete(item: Game)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: Int): Game?

    @Query("SELECT * FROM games ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY rating DESC")
    fun getTop(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE collections LIKE '%' || :collection || '%' ORDER BY rating DESC")
    fun getByCollection(collection: String): Flow<List<Game>>

    @Query("UPDATE games SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Double)

    @Query("UPDATE games SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE games SET completions = :count WHERE id = :id")
    suspend fun updateCompletions(id: Int, count: Int)

    @Query("SELECT * FROM games WHERE title LIKE '%' || :query || '%' OR universe LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Game>>
}

// ============================================
// КНИГИ
// ============================================
@Dao
interface BookDao {
    @Upsert
    suspend fun upsert(item: Book): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Book>)

    @Delete
    suspend fun delete(item: Book)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: Int): Book?

    @Query("SELECT * FROM books ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books ORDER BY rating DESC")
    fun getTop(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE collections LIKE '%' || :collection || '%' ORDER BY rating DESC")
    fun getByCollection(collection: String): Flow<List<Book>>

    @Query("UPDATE books SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Double)

    @Query("UPDATE books SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE books SET completions = :count WHERE id = :id")
    suspend fun updateCompletions(id: Int, count: Int)

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Book>>
}

// ============================================
// ФИЛЬМЫ
// ============================================
@Dao
interface MovieDao {
    @Upsert
    suspend fun upsert(item: Movie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Movie>)

    @Delete
    suspend fun delete(item: Movie)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getById(id: Int): Movie?

    @Query("SELECT * FROM movies ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY rating DESC")
    fun getTop(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE collections LIKE '%' || :collection || '%' ORDER BY rating DESC")
    fun getByCollection(collection: String): Flow<List<Movie>>

    @Query("UPDATE movies SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Double)

    @Query("UPDATE movies SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE movies SET completions = :count WHERE id = :id")
    suspend fun updateCompletions(id: Int, count: Int)

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR director LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Movie>>
}

// ============================================
// СЕРИАЛЫ
// ============================================
@Dao
interface SeriesDao {
    @Upsert
    suspend fun upsert(item: Series): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Series>)

    @Delete
    suspend fun delete(item: Series)

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun getById(id: Int): Series?

    @Query("SELECT * FROM series ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Series>>

    @Query("SELECT * FROM series ORDER BY rating DESC")
    fun getTop(): Flow<List<Series>>

    @Query("SELECT * FROM series WHERE collections LIKE '%' || :collection || '%' ORDER BY rating DESC")
    fun getByCollection(collection: String): Flow<List<Series>>

    @Query("UPDATE series SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Double)

    @Query("UPDATE series SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, newStatus: String)

    @Query("UPDATE series SET completions = :count WHERE id = :id")
    suspend fun updateCompletions(id: Int, count: Int)

    @Query("UPDATE series SET seasonsWatched = :count WHERE id = :id")
    suspend fun updateSeasonsWatched(id: Int, count: Int)

    @Query("UPDATE series SET episodesWatched = :count WHERE id = :id")
    suspend fun updateEpisodesWatched(id: Int, count: Int)

    @Query("SELECT * FROM series WHERE title LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Series>>
}

// ============================================
// МУЗЫКА
// ============================================
@Dao
interface MusicDao {
    @Upsert
    suspend fun upsert(item: Music): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Music>)

    @Delete
    suspend fun delete(item: Music)

    @Query("DELETE FROM music WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM music WHERE id = :id")
    suspend fun getById(id: Int): Music?

    @Query("SELECT * FROM music ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Music>>

    @Query("SELECT * FROM music ORDER BY rating DESC")
    fun getTop(): Flow<List<Music>>

    @Query("SELECT * FROM music WHERE collections LIKE '%' || :collection || '%' ORDER BY rating DESC")
    fun getByCollection(collection: String): Flow<List<Music>>

    @Query("UPDATE music SET rating = :newRating WHERE id = :id")
    suspend fun updateRating(id: Int, newRating: Double)

    @Query("UPDATE music SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Int, newStatus: String)

    @Query("SELECT * FROM music WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Music>>
}