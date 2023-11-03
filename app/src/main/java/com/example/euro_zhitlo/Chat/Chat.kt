package com.example.euro_zhitlo.Chat

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Chat(
    var chatId: String,
    var refugee_uid: String,
    var landlord_uid: String
) : Parcelable {

    constructor() : this( "","", "")

    // Зберегти об'єкт com.example.euro_zhitlo.Chat.Chat в базі даних
    fun saveToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val chatsRef: DatabaseReference = database.getReference("chats")
        val chatRef: DatabaseReference = chatsRef.push() // Генерує унікальний ключ

        // Отримати згенерований chatId
        val chatId = chatRef.key

        // Присвоїти chatId до поля об'єкту Chat
        if (chatId != null) {
            this.chatId = chatId
        }

        // Зберегти об'єкт у базу даних
        chatRef.setValue(this)
    }
    companion object {
        // Зчитати всі чати, в яких є певний refugee за його uid
        fun getChatsForRefugee(refugeeUid: String, callback: (List<Chat>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val chatsRef: DatabaseReference = database.getReference("chats")

            chatsRef.orderByChild("refugee_uid").equalTo(refugeeUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val chatList = mutableListOf<Chat>()
                        for (chatSnapshot in dataSnapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            if (chat != null) {
                                chatList.add(chat)
                            }
                        }
                        callback(chatList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        callback(emptyList())
                    }
                })
        }

        // Зчитати всі чати, в яких є певний landlord за його uid
        fun getChatsForLandlord(landlordUid: String, callback: (List<Chat>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val chatsRef: DatabaseReference = database.getReference("chats")

            chatsRef.orderByChild("landlord_uid").equalTo(landlordUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val chatList = mutableListOf<Chat>()
                        for (chatSnapshot in dataSnapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            if (chat != null) {
                                chatList.add(chat)
                            }
                        }
                        callback(chatList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        callback(emptyList())
                    }
                })
        }
    }
}
