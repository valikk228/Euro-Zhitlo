package com.example.euro_zhitlo.Apartment

import android.content.Context
import android.graphics.drawable.Drawable
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
        val message: TextView = itemView.findViewById(R.id.textView_message)
        val time: TextView = itemView.findViewById(R.id.textView_timeMessage)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    private fun getCrossFadeFactory(): DrawableCrossFadeFactory {
        return DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    }
}


