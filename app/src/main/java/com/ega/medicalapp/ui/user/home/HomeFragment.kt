package com.ega.medicalapp.ui.user.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.ega.medicalapp.data.model.UserEntity
import com.ega.medicalapp.ui.user.health.article.ArticleAdapter
import com.ega.medicalapp.ui.user.journal.JournalActivity
import com.ega.medicalapp.ui.user.profile.ProfileActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorage.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvArticle.setHasFixedSize(true)
        rvArticle.layoutManager = LinearLayoutManager(context)

        Firebase.database.getReference("/users/" + auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userEntity: UserEntity? = dataSnapshot.getValue(UserEntity::class.java)
                    val name = getString(R.string.greeting) + userEntity?.name
                    tvName.text = name
                    GlideApp.with(this@HomeFragment)
                        .load(storage.getReferenceFromUrl(userEntity?.photo.toString()))
                        .into(imgProfilePhoto)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        Firebase.database.getReference("/articles/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val articleEntities: ArrayList<ArticleEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children){
                        articleEntities.add(childSnapshot.getValue(ArticleEntity::class.java)!!)
                    }

                    val adapter = ArticleAdapter(articleEntities)
                    adapter.notifyDataSetChanged()
                    rvArticle.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        imgProfile.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnToJournal.setOnClickListener {
            val intent = Intent(activity, JournalActivity::class.java)
            startActivity(intent)
        }

    }
}
