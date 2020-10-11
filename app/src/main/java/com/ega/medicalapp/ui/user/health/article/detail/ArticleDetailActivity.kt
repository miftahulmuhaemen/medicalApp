package com.ega.medicalapp.ui.user.health.article.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.ui.user.health.article.ArticleViewHolder.Companion.ARTICLE_EXTRA
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_article.*

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        storage = FirebaseStorage.getInstance()

        val articleEntity : ArticleEntity? = intent.getParcelableExtra(ARTICLE_EXTRA)
        if(articleEntity!=null){
            tvTitle.text = articleEntity.title
            tvDate.text = articleEntity.date
            tvDescription.text = articleEntity.description
        GlideApp.with(this)
            .load(storage.getReferenceFromUrl(articleEntity.url.toString()))
            .into(imgArticle)
        } else {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }

    }


}