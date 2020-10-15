package com.ega.medicalapp.ui.user.health.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ArticleEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_health_article.*

class ArticleFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvArticle.setHasFixedSize(true)
        rvArticle.layoutManager = LinearLayoutManager(context)

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
                        activity, "Article load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}