package com.example.euro_zhitlo.Apartment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val context: Context, private val chats: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(chat: Chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            User.getUserByUid(user.uid) { currentUser ->
                if (currentUser != null) {
                    when (currentUser.type) {
                        "refugee" -> {
                            User.getUserByUid(chat.landlord_uid) { landlord_user ->

                                if (landlord_user != null) {
                                    holder.nickname.text = landlord_user.nickname
                                    landlord_user.getBitmapFromFirebaseStorage {image->
                                        holder.imageView.setImageBitmap(image)
                                    }
                                }

                            }
                        }
                        "landlord" -> {
                            User.getUserByUid(chat.refugee_uid) { refugee_user ->
                                if (refugee_user != null) {
                                    holder.nickname.text = refugee_user.nickname
                                    refugee_user.getBitmapFromFirebaseStorage {image->
                                        holder.imageView.setImageBitmap(image)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView1)
        val nickname: TextView = itemView.findViewById(R.id.textView_nickname)
        val message: TextView = itemView.findViewById(R.id.textView_message)
        val time: TextView = itemView.findViewById(R.id.textView_timeMessage)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
}


