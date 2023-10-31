package com.example.euro_zhitlo.Apartment

import Apartment
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.R

class ApartmentAdapter(private val context: Context, private val apartments: List<Apartment>) : RecyclerView.Adapter<ApartmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.apartment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apartments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apartment = apartments[position]

        // Отримуємо Bitmap для фотографії з Firebase Storage
        apartment.getBitmapFromFirebaseStorage { bitmap ->
            if (bitmap != null) {
                // Встановлюємо Bitmap у ImageView, якщо він доступний
                holder.imageView.setImageBitmap(bitmap)
            }
        }

        holder.titleTextView.text = apartment.title
        holder.locationTextView.text = apartment.location
        holder.priceTextView.text = apartment.price.toString() + "€ /"
        holder.accessTextView.text = if (apartment.access) "Availiable" else "Booked"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView1) // Оновлено для ImageView
        val titleTextView: TextView = itemView.findViewById(R.id.textView_title)
        val locationTextView: TextView = itemView.findViewById(R.id.textView_location)
        val priceTextView: TextView = itemView.findViewById(R.id.textView_price)
        val accessTextView: TextView = itemView.findViewById(R.id.textView_access)
    }
}
