package com.ega.medicalapp.ui.user.health.meditation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.data.model.MeditationEntity
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_article_fit.view.*

class MeditationAdapter (private val meditation: ArrayList<MeditationEntity>) : RecyclerView.Adapter<MeditationViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article_fit, parent, false)
        return MeditationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeditationViewHolder, position: Int) {
        holder.bind(meditation[position])
    }

    override fun getItemCount(): Int = meditation.size

}

class MeditationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val MEDITATION_EXTRA = "MEDITATION_EXTRA"
    }

    fun bind(meditationEntity: MeditationEntity) {

        val storage: FirebaseStorage = FirebaseStorage.getInstance()

        with(itemView) {
            tvTitle.text = meditationEntity.title
            tvDescription.text = meditationEntity.description
            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    ArticleDetailActivity::class.java
                ).putExtra(MEDITATION_EXTRA, meditationEntity)
                itemView.context.startActivity(intent)
            }
        }
    }
}