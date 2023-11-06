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
import kotlinx.coroutines.flow.combineTransform
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val context: Context, private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val message = messages[viewType]

        val view: View
        val time: TextView
        val photo: ImageView
        val messageArea: TextView

        if (message.messageType == "sender" && message.photo.isNotEmpty()) {
            view = inflater.inflate(R.layout.photo_right_item, parent, false)
            time = view.findViewById(R.id.textView_timeMessage)
            photo = view.findViewById(R.id.imageView_photo)
            return PhotoRightViewHolder(view, time, photo)
        } else if (message.messageType == "sender") {
            view = inflater.inflate(R.layout.message_right_item, parent, false)
            time = view.findViewById(R.id.textView_timeMessage)
            messageArea = view.findViewById(R.id.textView_message)
            return MessageRightViewHolder(view, time, messageArea)
        } else if (message.photo.isNotEmpty()) {
            view = inflater.inflate(R.layout.photo_left_item, parent, false)
            time = view.findViewById(R.id.textView_timeMessage)
            photo = view.findViewById(R.id.imageView_photo)
            return PhotoLeftViewHolder(view, time, photo)
        } else {
            view = inflater.inflate(R.layout.message_left_item, parent, false)
            time = view.findViewById(R.id.textView_timeMessage)
            messageArea = view.findViewById(R.id.textView_message)
            return MessageLeftViewHolder(view, time, messageArea)
        }
    }



    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is PhotoRightViewHolder -> {
                holder.time.text = formatDate(message.time)
                message.getBitmapFromFirebaseStorage { image->
                    holder.photo.setImageBitmap(image)
                }
            }
            is MessageRightViewHolder -> {
                holder.time.text = formatDate(message.time)
                holder.messageArea.text = message.text
            }
            is PhotoLeftViewHolder -> {
                holder.time.text = formatDate(message.time)
                message.getBitmapFromFirebaseStorage { image->
                    holder.photo.setImageBitmap(image)
                }            }
            is MessageLeftViewHolder -> {
                holder.time.text = formatDate(message.time)
                holder.messageArea.text = message.text
            }
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        return format.format(date)
    }

    inner class MessageRightViewHolder(itemView: View, val time: TextView, val messageArea: TextView) : RecyclerView.ViewHolder(itemView)

    inner class PhotoRightViewHolder(itemView: View, val time: TextView, val photo: ImageView) : RecyclerView.ViewHolder(itemView)

    inner class MessageLeftViewHolder(itemView: View, val time: TextView, val messageArea: TextView) : RecyclerView.ViewHolder(itemView)

    inner class PhotoLeftViewHolder(itemView: View, val time: TextView, val photo: ImageView) : RecyclerView.ViewHolder(itemView)
}
