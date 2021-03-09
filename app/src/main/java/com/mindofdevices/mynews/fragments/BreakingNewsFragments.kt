package com.mindofdevices.mynews.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindofdevices.mynews.MainActivity
import com.mindofdevices.mynews.R
import com.mindofdevices.mynews.adapters.NewsAdapter
import com.mindofdevices.mynews.ui.NewsViewModel
import com.mindofdevices.mynews.util.constants.Companion.QUERY_PAGE_SIZE
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragments: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel:NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                    R.id.action_breakingNewsFragments_to_articleFragment,
                    bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is com.mindofdevices.mynews.util.Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {  newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastpage = viewModel.breakingNewsPage == totalPages
                        if(isLastpage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is com.mindofdevices.mynews.util.Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error Occured: $message")
                    }
                }

                is com.mindofdevices.mynews.util.Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var  isLoading = false
    var isLastpage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastpage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }


        }
    }
    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragments.scrollListener)
        }
    }
}