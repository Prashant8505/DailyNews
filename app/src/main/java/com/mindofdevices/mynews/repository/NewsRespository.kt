package com.mindofdevices.mynews.repository

import com.mindofdevices.mynews.Article
import com.mindofdevices.mynews.api.RetrofitInstance
import com.mindofdevices.mynews.db.Database

class NewsRespository(
        val db: Database
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchForNews(searchquery: String, pageNumber: Int) =
            RetrofitInstance.api.searchForNews(searchquery, pageNumber)

    suspend fun upsert(article : Article) = db.getarticleDao().upsert(article)

    fun getSavednews() = db.getarticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getarticleDao().deleteArticle(article)


}