package com.mindofdevices.mynews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindofdevices.mynews.Article
import com.mindofdevices.mynews.NewsApplication
import com.mindofdevices.mynews.newsResponse
import com.mindofdevices.mynews.repository.NewsRespository
import com.mindofdevices.mynews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
        val newsRespository: NewsRespository
): AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<newsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    var breakingNewsResponse: newsResponse? = null

    var searchNews: MutableLiveData<Resource<newsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: newsResponse? = null

    init {
        getBreakingNews("us")
    }
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)

    }
        fun searchNews(searchQuery: String) = viewModelScope.launch {
           safeSearchNewsCall(searchQuery)

        }
    private fun handlingBreakingNewsResponse(response: Response<newsResponse>): Resource<newsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse == null)
                {
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticle?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
           return Resource.Error(response.message())
    }

    private fun handlingSearchNewsResponse(response: Response<newsResponse>): Resource<newsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if(searchNewsResponse == null)
                {
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticle = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticle?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch{
                         newsRespository.upsert(article)
    }

    fun getSavedNews() = newsRespository.getSavednews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRespository.deleteArticle(article)
    }
    private suspend fun safeSearchNewsCall(searchQuery: String) {

        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRespository.searchForNews(searchQuery, searchNewsPage)
                searchNews.postValue(handlingSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRespository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handlingBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun hasInternetConnection(): Boolean{

        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}