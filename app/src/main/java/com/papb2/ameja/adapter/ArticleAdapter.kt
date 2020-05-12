package com.papb2.ameja.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.papb2.ameja.R
import com.papb2.ameja.model.Article
import kotlinx.android.synthetic.main.item_article.view.*


class ArticleAdapter :
        RecyclerView.Adapter<ArticleAdapter.CardViewViewHolder>() {

    var listArticle = ArrayList<Article>()

    fun setData(items: ArrayList<Article>) {
        listArticle.clear()
        listArticle.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CardViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int = listArticle.size

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(listArticle[position])
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(article: Article) {
            with(itemView) {
                if(article.imgUrl.isNullOrEmpty() || article.imgUrl.isNullOrBlank())
                    ivThumbnail.visibility = View.GONE
                else
                    Glide.with(context).load(article.imgUrl).apply(RequestOptions.centerCropTransform()).into(ivThumbnail)
                tvTitle.text = article.title
            }

        }
    }
}