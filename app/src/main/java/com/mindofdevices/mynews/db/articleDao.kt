package com.mindofdevices.mynews.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mindofdevices.mynews.Article

@Dao
interface articleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}