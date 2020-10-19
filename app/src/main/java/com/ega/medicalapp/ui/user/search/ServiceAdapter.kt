package com.ega.medicalapp.ui.user.search

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ServiceEntity
import kotlinx.android.synthetic.main.item_search_fit.view.*


class ServiceAdapter(private val service: ArrayList<ServiceEntity>) : RecyclerView.Adapter<ServiceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search_fit,
            parent,
            false
        )
        return ServiceViewHolder(view)
    }
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(service[position])
    }
    override fun getItemCount(): Int = service.size
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(service: ServiceEntity) {

        with(itemView) {
            tvName.text = service.name
            tvAddress.text = service.address
            tvMap.text = service.map
            tvTime.text = service.time
            tvWeb.text = service.website
            tvPhone.text = service.phone

            tvMap.setOnClickListener {
                val gmmIntentUri = Uri.parse(service.map)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(itemView.context.packageManager)?.let {
                    startActivity(itemView.context,mapIntent, null)
                }
            }
        }
    }
}