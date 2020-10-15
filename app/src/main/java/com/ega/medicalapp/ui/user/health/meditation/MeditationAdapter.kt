package com.ega.medicalapp.ui.user.health.meditation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.MeditationEntity
import com.ega.medicalapp.ui.user.health.meditation.mediaplayer.MediaPlayerFragment
import kotlinx.android.synthetic.main.item_meditation_fit.view.*

class MeditationAdapter (private val meditation: ArrayList<MeditationEntity>, private val activity: AppCompatActivity) : RecyclerView.Adapter<MeditationViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meditation_fit, parent, false)
        return MeditationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeditationViewHolder, position: Int) {
        holder.bind(meditation[position], activity)
    }

    override fun getItemCount(): Int = meditation.size

}

class MeditationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(meditationEntity: MeditationEntity, context: AppCompatActivity) {

        with(itemView) {
            tvTitle.text = meditationEntity.title
            tvDescription.text = meditationEntity.description
            itemView.setOnClickListener {
                context.supportFragmentManager.beginTransaction().replace(R.id.flUser, MediaPlayerFragment()).addToBackStack("TAG").commit()
            }
        }
    }
}