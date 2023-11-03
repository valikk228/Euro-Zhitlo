package com.example.euro_zhitlo.Chat

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Message(
    val chat_id: String,
    val uid_sender: String,
    val uid_receiver: String,
    val photo: Boolean,
    val text: String,
    val time: Date
) : Parcelable {

    constructor() : this("","", "", false, "", Date(0))

    // Зберегти об'єкт com.example.euro_zhitlo.Chat.Message в базі даних
    fun saveToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val messagesRef: DatabaseReference = database.getReference("messages")
        val messageRef: DatabaseReference = messagesRef.push()
        messageRef.setValue(this)
    }

    companion object {
        // Прочитати об'єкт com.example.euro_zhitlo.Chat.Message з бази даних за ключем (наприклад, messageKey)
        fun readFromDatabase(messageKey: String, callback: (Message?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val messagesRef: DatabaseReference = database.getReference("messages").child(messageKey)

            messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    callback(message)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(null)
                }
            })
        }
    }
}
