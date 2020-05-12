package com.papb2.ameja.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.papb2.ameja.R
import com.papb2.ameja.adapter.ArticleAdapter
import com.papb2.ameja.viewmodel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment() {

    private var adapter: ArticleAdapter = ArticleAdapter()
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        rvArticles.layoutManager = LinearLayoutManager(requireContext())
        rvArticles.adapter = adapter
        adapter.notifyDataSetChanged()

        articleViewModel = ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
        ).get(ArticleViewModel::class.java)
        articleViewModel.setArticles()

        articleViewModel.getArticle().observe(viewLifecycleOwner, Observer { article ->
            if (article != null) {
                adapter.setData(article)
            }
        })

    }
}