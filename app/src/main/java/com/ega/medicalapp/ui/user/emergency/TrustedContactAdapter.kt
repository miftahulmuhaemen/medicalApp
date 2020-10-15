package com.ega.medicalapp.ui.user.emergency

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.data.model.EmergencyEntity
import com.ega.medicalapp.ui.user.health.article.detail.ArticleDetailActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_user.*
import kotlinx.android.synthetic.main.item_article_fit.view.*
import kotlinx.android.synthetic.main.item_emergency.view.*

class TrustedContactAdapter (private val emergencies: ArrayList<EmergencyEntity>) : RecyclerView.Adapter<ArticleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emergency, parent, false)
        return ArticleViewHolder(view)
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(emergencies[position])
    }
    override fun getItemCount(): Int = emergencies.size
}

class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val auth = FirebaseAuth.getInstance()

    fun bind(emergency: EmergencyEntity) {

        with(itemView) {
            tvName.text = emergency.name
            tvContact.text = emergency.phone

            val popup = PopupMenu(itemView.context, btnMore)
            popup.menuInflater.inflate(R.menu.menu_contact, popup.menu)

            btnMore.setOnClickListener {
                popup.show()
            }

            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.mDelete -> {
                        Firebase.database.reference
                            .child("emergency")
                            .child(auth.currentUser?.uid.toString())
                            .orderByChild("phone")
                            .equalTo(emergency.phone)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (childSnapshot in dataSnapshot.children){
                                        Firebase.database.reference
                                            .child("emergency")
                                            .child(auth.currentUser?.uid.toString())
                                            .child(childSnapshot.key.toString())
                                            .removeValue()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                        true
                    }
                    else -> false
                }
            }
        }
    }
}