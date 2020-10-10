package com.ega.medicalapp.ui.user.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.Article
import com.ega.medicalapp.data.model.User
import com.ega.medicalapp.util.GlideApp
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: FirebaseRecyclerAdapter<Article, ArticleViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()

        val baseQuery = Firebase.database.getReference("/articles/")

        val options: FirebaseRecyclerOptions<Article> = FirebaseRecyclerOptions.Builder<Article>()
            .setLifecycleOwner(this)
            .setQuery(baseQuery, Article::class.java)
            .build()

        adapter =
            object : FirebaseRecyclerAdapter<Article, ArticleViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ArticleViewHolder {
                    return ArticleViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                            R.layout.item_article_fit,
                            parent,
                            false
                        )
                    )
                }

                override fun onBindViewHolder(
                    holder: ArticleViewHolder,
                    position: Int,
                    model: Article
                ) {
                    holder.bind(model)
                }

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvArticle.setHasFixedSize(true)
        rvArticle.layoutManager = LinearLayoutManager(context)
        rvArticle.adapter = adapter

        Firebase.database.getReference("/users/" + auth.currentUser?.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
                    tvName.append(user?.name)
                    GlideApp.with(this@HomeFragment)
                        .load(storage.getReferenceFromUrl(user?.photo.toString()))
                        .into(imgProfilePhoto)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

//        Firebase.database.getReference("/articles/")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    var articles: DataSnapshot = dataSnapshot
////                    var articles : PagedList<Article> = LiveData<Article>()
////                    for (childSnapshot in dataSnapshot.children){
////                        articles.add(childSnapshot.getValue(Article::class.java)!!)
////                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(
//                        activity, "Registration failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })

    }
}
