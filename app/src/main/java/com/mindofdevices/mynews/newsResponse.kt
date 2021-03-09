package com.mindofdevices.mynews

data class newsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)