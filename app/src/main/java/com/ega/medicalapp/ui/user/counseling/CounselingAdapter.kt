package com.ega.medicalapp.ui.user.counseling

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.AppointmentEntity
import com.ega.medicalapp.data.model.PsychologistEntity
import com.ega.medicalapp.ui.user.counseling.chat.ChatUserActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_counseling_fit.view.*
import java.util.*

class CounselingAdapter(
    private val activity: AppCompatActivity,
    private val psychologistEntity: ArrayList<PsychologistEntity>,
    private val psychologistAppointments: ArrayList<AppointmentEntity>,
    private val psychologistUIDs: ArrayList<String>
) : RecyclerView.Adapter<CounselingViewHolder>(), Filterable {

    private var psychologistFilter = psychologistEntity

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    psychologistFilter = psychologistEntity
                } else {
                    val resultList = ArrayList<PsychologistEntity>()
                    for (row in psychologistEntity) {
                        if (row.name?.toLowerCase(Locale.ROOT)
                                ?.contains(charSearch.toLowerCase(Locale.ROOT))!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    psychologistFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = psychologistFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                psychologistFilter = results?.values as ArrayList<PsychologistEntity>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounselingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_counseling_fit, parent, false)
        return CounselingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CounselingViewHolder, position: Int) {
        holder.bind(
            psychologistFilter[position],
            psychologistAppointments[position],
            psychologistUIDs[position],
            activity
        )
    }

    override fun getItemCount(): Int = psychologistFilter.size

}

class CounselingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val CHAT_PSYCHOLOGIST = "x032"
        const val CHAT_APPOINTMENT = "x033"
    }

    fun bind(
        psychologistEntity: PsychologistEntity,
        psychologistAppointment: AppointmentEntity,
        psychologistUID: String,
        activity: AppCompatActivity
    ) {

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val storage: FirebaseStorage = FirebaseStorage.getInstance()

        with(itemView) {

            Log.d("CHECK", psychologistEntity.toString())

            tvPsychologistName.text = psychologistEntity.name
            tvExperience.text = psychologistEntity.experience
            GlideApp.with(itemView.context)
                .load(storage.getReferenceFromUrl(psychologistEntity.photo.toString()))
                .into(imgPsychologist)

            when (psychologistAppointment.status) {
                itemView.resources.getString(R.string.waiting) -> {
                    btnChat.text = activity.resources.getString(R.string.waiting)
                }
                itemView.resources.getString(R.string.accept) -> {
                    btnChat.text = activity.resources.getString(R.string.chat)
                    btnChat.setOnClickListener {
                        val intent = Intent(
                            itemView.context,
                            ChatUserActivity::class.java
                        )
                            .putExtra(CHAT_APPOINTMENT, psychologistAppointment)
                            .putExtra(CHAT_PSYCHOLOGIST, psychologistEntity)

                        itemView.context.startActivity(intent)
                    }
                }
                else -> {
                    btnChat.setOnClickListener {

                        val user = auth.currentUser
                        val randomID = UUID.randomUUID().toString()
                        val photoURL : String = (user?.photoUrl ?: "gs://medicalapp-e2fc9.appspot.com/118780058_1806454849507718_4343235856376208408_n.jpg").toString()

                        val post = AppointmentEntity(
                            randomID,
                            user?.uid,
                            psychologistUID,
                            itemView.context.resources.getString(R.string.waiting),
                            photoURL,
                            user?.displayName
                        )

                        val postValue = post.toMap()
                        val childUpdate = hashMapOf<String, Any>(
                            "appointments/${randomID}" to postValue
                        )

                        Firebase.database.reference.updateChildren(childUpdate)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        activity, "Request success.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnChat.text = activity.resources.getString(R.string.waiting)
                                } else {
                                    Toast.makeText(
                                        activity, "Request failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
            }

            if (!psychologistEntity.online!!) {
                btnChat.isEnabled = false
            }
        }
    }
}