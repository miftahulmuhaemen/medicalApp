package com.ega.medicalapp.ui.user.counseling.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.MessagesEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_counseling_chat_receive.view.*
import kotlinx.android.synthetic.main.item_counseling_chat_send.view.*

class ChatAdapter (private val messagesEntity: ArrayList<MessagesEntity>) : RecyclerView.Adapter<ChatViewHolder>(){

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int): Int {
        val currentUserUID: String = auth.currentUser?.uid.toString()
        return if(messagesEntity[position].sender.toString() != currentUserUID)
            0
        else
            1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(if(viewType == 1)
             LayoutInflater.from(parent.context).inflate(R.layout.item_counseling_chat_send, parent, false)
         else
             LayoutInflater.from(parent.context).inflate(R.layout.item_counseling_chat_receive, parent, false))
    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messagesEntity[position])
    }
    override fun getItemCount(): Int = messagesEntity.size
}

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(messagesEntity: MessagesEntity) {

        with(itemView) {
            when(itemViewType){
                0 -> tvChatReceive.text = messagesEntity.message
                1 -> tvChatSender.text = messagesEntity.message
            }
        }
    }
}