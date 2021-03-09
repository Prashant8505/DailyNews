package com.mindofdevices.mynews.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mindofdevices.mynews.MainActivity
import com.mindofdevices.mynews.R
import com.mindofdevices.mynews.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        fab.setOnClickListener{
               viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully!!", Snackbar.LENGTH_SHORT).show()
        }
    }
}