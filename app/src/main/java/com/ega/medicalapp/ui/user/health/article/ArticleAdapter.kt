package com.ega.medicalapp.ui.user.health.article

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_article_fit.view.*

class ArticleAdapter (private val articleEntities: ArrayList<ArticleEntity>) : RecyclerView.Adapter<ArticleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article_fit, parent, false)
        return ArticleViewHolder(view)
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articleEntities[position])
    }
    override fun getItemCount(): Int = articleEntities.size
}

class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val ARTICLE_EXTRA = "ARTICLE_EXTRA"
    }

    fun bind(articleEntity: ArticleEntity) {

        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        with(itemView) {
            tvTitle.text = articleEntity.title
            tvDescription.text = articleEntity.description
            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    ArticleDetailActivity::class.java
                ).putExtra(ARTICLE_EXTRA, articleEntity)
                itemView.context.startActivity(intent)
            }
            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(articleEntity.url.toString()))
                .into(imgArticle)
        }
    }
}