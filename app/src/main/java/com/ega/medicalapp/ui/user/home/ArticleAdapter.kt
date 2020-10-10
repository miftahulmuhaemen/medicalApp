package com.ega.medicalapp.ui.user.home

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.data.model.Article
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_article_fit.view.*

class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(article: Article) {

        lateinit var storage: FirebaseStorage

        with(itemView) {
            tvTitle.text = article.title
            tvDescription.text = article.description
            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    ArticleDetailActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                itemView.context.startActivity(intent)
            }
            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(article.url.toString()))
                .into(imgArticle)
        }
    }
}
