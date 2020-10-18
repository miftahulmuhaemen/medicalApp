package com.ega.medicalapp.ui.psychologist.new

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.AppointmentEntity
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.data.model.MessagesEntity
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_article_fit.view.*
import kotlinx.android.synthetic.main.item_new_fit.view.*

class NewAdapter (private val appointments: ArrayList<AppointmentEntity>) : RecyclerView.Adapter<NewViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_fit, parent, false)
        return NewViewHolder(view)
    }
    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        holder.bind(appointments[position])
    }
    override fun getItemCount(): Int = appointments.size
}

class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(appointment: AppointmentEntity) {

        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val database = Firebase.database.reference
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        with(itemView) {

            tvPatientName.text = appointment.patientname

            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(appointment.photo.toString()))
                .into(imgProfile)

            btnAccept.setOnClickListener {

                val currentUserUID = auth.currentUser?.uid.toString()
                val timestamp = System.currentTimeMillis()
                val postChannel = ChannelEntity(
                    "",
                    false,
                    appointment.patient.toString(),
                    appointment.psychologist.toString(),
                    timestamp,
                    appointment.patientname.toString(),
                    appointment.photo.toString(),
                    appointment.id.toString()
                )
                val postChannelValue = postChannel.toMap()
                val childChannelUpdate = hashMapOf<String, Any>(
                    "chats/channels/${appointment.id}" to postChannelValue
                )
                database.updateChildren(childChannelUpdate)

                        database
                            .child("appointments")
                            .child(appointment.id.toString())
                            .child("status")
                            .setValue(itemView.resources.getString(R.string.accept)).addOnSuccessListener {
                                /** Move intent to Chat */
                            }

            }

            btnReject.setOnClickListener {
                database
                    .child("appointments")
                    .child(appointment.id.toString())
                    .removeValue()
            }

        }
    }
}