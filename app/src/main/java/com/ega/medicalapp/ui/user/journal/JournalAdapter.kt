package com.ega.medicalapp.ui.user.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.JournalEntity
import kotlinx.android.synthetic.main.item_article_fit.view.tvDescription
import kotlinx.android.synthetic.main.item_journal_fit.view.*

class JournalAdapter (private val journalEntity: ArrayList<JournalEntity>) : RecyclerView.Adapter<JournalViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_journal_fit, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(journalEntity[position])
    }

    override fun getItemCount(): Int = journalEntity.size

}

class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(journalEntity: JournalEntity) {
        with(itemView) {
            tvDate.text = journalEntity.date
            tvDescription.text = journalEntity.description
        }
    }
}