package com.mindofdevices.mynews.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindofdevices.mynews.repository.NewsRespository

class NewsViemModelProviderFactory(
    val app: Application,
        val newsRespository: NewsRespository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRespository) as T
    }
}