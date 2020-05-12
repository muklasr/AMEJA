package com.papb2.ameja.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.papb2.ameja.model.Article
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class ArticleViewModel : ViewModel() {
    companion object {
        private val API_KEY = "gCEI14lwbvGzmrVAIHT2WqzSoKeeAn32"
    }

    val listArticles = MutableLiveData<ArrayList<Article>>()

    internal fun setArticles() {
//        var language = "en-US"
//        if (Locale.getDefault().displayLanguage == "Indonesia")
//            language = "id"
        val client = AsyncHttpClient()
        val listItems = ArrayList<Article>()
        val url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=personal%20development&api-key=$API_KEY&fq=news_desk:%20(%22Education%22%20%22Health%22)&type_of_material=Article&facet=true"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val res = responseObject.getJSONObject("response")
                    val list = res.getJSONArray("docs")
                    for (i in 0 until list.length()) {
                        val article = list.getJSONObject(i)
                        val headline = article.getJSONObject("headline")
                        val multimediaArray = article.getJSONArray("multimedia")
                        var imgUrl = ""
                        if (multimediaArray.length() > 0) {
                            val multimediaObject = multimediaArray.getJSONObject(0)
                            imgUrl = "https://www.nytimes.com/"+multimediaObject.getString("url")
                        }
                        Log.d("TES", imgUrl)
                        val articleItems = Article(
                                headline.getString("main"),
                                imgUrl
                        )
                        listItems.add(articleItems)
                    }
                    listArticles.postValue(listItems)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    internal fun getArticle(): LiveData<ArrayList<Article>> {
        return listArticles
    }
}