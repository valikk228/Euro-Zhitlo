package com.example.euro_zhitlo.Apartment

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val context: Context, private var chats: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var Chats: List<Chat> = chats
    private var itemClickListener: OnItemClickListener? = null
    private val mAuth = FirebaseAuth.getInstance()
    private val user = mAuth.currentUser

    interface OnItemClickListener {
        fun onItemClick(chat: Chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Chats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = Chats[position]


        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(chat)
        }

        if (user != null) {
            User.getUserByUid(user.uid) { currentUser ->
                if (currentUser != null) {
                    when (currentUser.type) {
                        "refugee" -> {
                            User.getUserByUid(chat.landlord_uid) { landlord_user ->
                                if (landlord_user != null) {
                                    holder.nickname.text = landlord_user.nickname
                                    loadAndDisplayImage(holder.imageView, landlord_user.image)
                                }
                            }
                        }
                        "landlord" -> {
                            User.getUserByUid(chat.refugee_uid) { refugee_user ->
                                if (refugee_user != null) {
                                    holder.nickname.text = refugee_user.nickname
                                    loadAndDisplayImage(holder.imageView, refugee_user.image)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun filterChats(query: String) {
        val filteredChats = mutableListOf<Chat>()

        for (chat in chats) {
            // Отримуємо nickname користувача залежно від його типу
            val userId = if (getCurrentUserType() == "landlord") chat.refugee_uid else chat.landlord_uid

            User.getUserByUid(userId) { user ->
                val nickname = user?.nickname ?: ""

                // Фільтруємо за nickname
                if (nickname.contains(query, ignoreCase = true)) {
                    filteredChats.add(chat)
                }

                // Якщо це останній чат, оновлюємо дані у RecyclerView
                if (chat == chats.last()) {
                    filteredChats(filteredChats)
                }
            }
        }
    }

    private fun getCurrentUserType(): String {
        var type = ""
        user?.let {
            User.getUserByUid(it.uid){currentUser->
                if (currentUser != null) {
                    if(currentUser.type == "landlord") type = "landlord"
                    else{type = "refugee"}
                }
            }
        }
        return type
    }

    fun filteredChats(filteredChats: List<Chat>) {
        Chats = filteredChats
        notifyDataSetChanged()
    }


    fun setChats(newChats: List<Chat>) {
        chats = newChats
        filterChats("") // При зміні чатів також фільтруємо за порожнім рядком, щоб показати весь список
    }

    private fun loadAndDisplayImage(imageView: ImageView, imageUrl: String?) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        val activity = (context as? AppCompatActivity)
        if (activity != null && !activity.isDestroyed && !activity.isFinishing) {
            Glide.with(activity)
                .load(imageUrl)
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .transition(DrawableTransitionOptions.withCrossFade(getCrossFadeFactory()))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        circularProgressDrawable.stop()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        circularProgressDrawable.stop()
                        return false
                    }
                })
                .into(imageView)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView1)
        val nickname: TextView = itemView.findViewById(R.id.textView_nickname)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    private fun getCrossFadeFactory(): DrawableCrossFadeFactory {
        return DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    }
}



