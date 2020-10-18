package com.ega.medicalapp.ui.psychologist.progress

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.AppointmentEntity
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.data.model.MessagesEntity
import com.ega.medicalapp.ui.psychologist.counseling.ChatPsychologistActivity
import com.ega.medicalapp.ui.psychologist.counseling.chat.ChatPsychologistFragment
import com.ega.medicalapp.ui.user.counseling.CounselingViewHolder
import com.ega.medicalapp.ui.user.counseling.chat.ChatUserActivity
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_inprogress_fit.view.*

class ProgressAdapter (private val channels: ArrayList<ChannelEntity>) : RecyclerView.Adapter<ProgressViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inprogress_fit, parent, false)
        return ProgressViewHolder(view)
    }
    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(channels[position])
    }
    override fun getItemCount(): Int = channels.size
}

class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val CHAT_CHANNEL = "x038"
    }

    fun bind(channel: ChannelEntity) {

        val storage: FirebaseStorage = FirebaseStorage.getInstance()

        with(itemView) {

            tvPatientName.text = channel.patientname
            tvLastChat.text = channel.lastMessage
            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(channel.patientphoto.toString()))
                .into(imgProfile)

            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    ChatPsychologistActivity::class.java
                ).putExtra(CHAT_CHANNEL, channel)

                itemView.context.startActivity(intent)
            }
        }
    }
}