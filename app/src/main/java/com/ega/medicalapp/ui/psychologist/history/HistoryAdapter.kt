package com.ega.medicalapp.ui.psychologist.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.ui.psychologist.counseling.ChatPsychologistActivity
import com.ega.medicalapp.ui.psychologist.progress.ProgressViewHolder
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_history_fit.view.*
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter (private val channels: ArrayList<ChannelEntity>) : RecyclerView.Adapter<HistoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_fit, parent, false)
        return HistoryViewHolder(view)
    }
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(channels[position])
    }
    override fun getItemCount(): Int = channels.size
}

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val CHAT_HISTORY = "x821"
    }

    fun bind(channel: ChannelEntity) {

        val storage: FirebaseStorage = FirebaseStorage.getInstance()

        with(itemView) {

            tvPatientName.text = channel.patientname
            tvDate.text = getDateTime(channel.timestamp.toString())
            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(channel.patientphoto.toString()))
                .into(imgProfile)

            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    ChatPsychologistActivity::class.java
                )
                    .putExtra(ProgressViewHolder.CHAT_CHANNEL, channel)
                    .putExtra(CHAT_HISTORY, true)

                itemView.context.startActivity(intent)
            }
        }
    }

    private fun getDateTime(timestamp: String): String? {
        return try {
            val date = Date(timestamp.toLong())
            date.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }
}