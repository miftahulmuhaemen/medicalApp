package com.ega.medicalapp.ui.psychologist.counseling

import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.ui.psychologist.counseling.chat.ChatPsychologistFragment
import com.ega.medicalapp.ui.psychologist.counseling.journal.JournalPsychologistFragment
import com.ega.medicalapp.ui.psychologist.history.HistoryViewHolder.Companion.CHAT_HISTORY
import com.ega.medicalapp.ui.psychologist.progress.ProgressViewHolder.Companion.CHAT_CHANNEL
import com.ega.medicalapp.util.GlideApp
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_psychologist.*


class ChatPsychologistActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var channel = ChannelEntity()
    private var isHistory = false

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_psychologist)

        if (intent != null) {
            channel = intent.getParcelableExtra(CHAT_CHANNEL)!!
            isHistory = intent.getBooleanExtra(CHAT_HISTORY, false)
        } else {
            finish()
        }

        val channelID = channel.id.toString()

        tvChatName.text = channel.patientname
        if (channel.patientphoto!!.isNotEmpty())
            GlideApp.with(this)
                .load(
                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(channel.patientphoto.toString())
                )
                .into(imgChatProfile)

        val bundle = Bundle()
        bundle.putParcelable(CHAT_CHANNEL, channel)
        bundle.putBoolean(CHAT_HISTORY, isHistory)

        val chatFragment = ChatPsychologistFragment()
        val journalFragment = JournalPsychologistFragment()
        var activeFragment: Fragment = chatFragment

        chatFragment.arguments = bundle
        journalFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.flPsychologistCounseling, journalFragment).hide(
                journalFragment
            ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flPsychologistCounseling, chatFragment)
            .commit()

        tabsChatPsychologist.addTab(
            tabsChatPsychologist
                .newTab()
                .setText(resources.getString(R.string.chat))
                .setIcon(resources.getDrawable(R.drawable.ic_chat, null))
        )

        tabsChatPsychologist.addTab(
            tabsChatPsychologist
                .newTab()
                .setText(resources.getString(R.string.journal))
                .setIcon(resources.getDrawable(R.drawable.ic_journal, null))
        )

        tabsChatPsychologist.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        supportFragmentManager.beginTransaction().hide(activeFragment)
                            .show(chatFragment).commit()
                        activeFragment = chatFragment
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().hide(activeFragment)
                            .show(journalFragment).commit()
                        activeFragment = journalFragment
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        val popup = PopupMenu(this, btnMore)
        popup.menuInflater.inflate(R.menu.menu_chat, popup.menu)

        btnMore.setOnClickListener {
            popup.show()
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mEndSessionUser -> {
                    database.child("chats")
                        .child("channels")
                        .child(channelID)
                        .child("endSession")
                        .setValue(true)
                        .addOnSuccessListener {
                            database
                                .child("appointments")
                                .child(channelID)
                                .removeValue()
                                .addOnSuccessListener {
                                    finish()
                                }
                        }

                    true
                }
                else -> false
            }
        }


        if(isHistory)
            btnMore.isEnabled = false
    }

}