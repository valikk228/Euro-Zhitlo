package com.example.euro_zhitlo.Apartment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Chat.Message
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val context: Context, private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view: View
        val inflater = LayoutInflater.from(context)

        // Отримуємо повідомлення для даної позиції
        val message = messages[position]

        // Перевіряємо, чи це повідомлення відправлене поточним користувачем чи отримане
        if (message.messageType == "sender") {
            view = inflater.inflate(R.layout.message_right_item, parent, false)
        } else {
            view = inflater.inflate(R.layout.message_left_item, parent, false)
        }

        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.message.text = message.text
        holder.time.text = formatDate(message.time)
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        return format.format(date)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.textView_message)
        val time: TextView = itemView.findViewById(R.id.textView_timeMessage)
    }
}

