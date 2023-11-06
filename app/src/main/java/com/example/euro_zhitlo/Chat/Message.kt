package com.example.euro_zhitlo.Chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.IOException
import java.util.Date

@Keep
@Parcelize
data class Message(
    var uid_sender: String,
    var uid_receiver: String,
    var photo: String,
    var text: String,
    var time: Date,
    var messageType: String
) : Parcelable {

    constructor() : this("", "", "", "", Date(0),"")

    // Зберегти об'єкт com.example.euro_zhitlo.Chat.Message в базі даних
    fun saveToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val messagesRef: DatabaseReference = database.getReference("messages")
        val messageRef: DatabaseReference = messagesRef.push()
        messageRef.setValue(this)
    }

    fun getBitmapFromFirebaseStorage(callback: (Bitmap?) -> Unit) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photo)

        try {
            val localFile = File.createTempFile("images", "jpg")
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                callback(bitmap)
            }.addOnFailureListener { exception ->
                // Обробка помилки, якщо виникла
                callback(null)
            }
        } catch (e: IOException) {
            // Обробка помилки створення тимчасового файлу
            callback(null)
        }
    }


    companion object {
        fun readMessagesForUser(userId: String, companionId: String, callback: (List<Message>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val messagesRef: DatabaseReference = database.getReference("messages")

            val messages = mutableListOf<Message>()

            messagesRef.orderByChild("uid_sender").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (messageSnapshot in dataSnapshot.children) {
                            val message = messageSnapshot.getValue(Message::class.java)
                            if (message != null && message.uid_receiver == companionId) {
                                message.messageType = "sender"
                                messages.add(message)
                            }
                        }

                        // Тепер у вас є список відправлених повідомлень

                        messagesRef.orderByChild("uid_sender").equalTo(companionId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot2: DataSnapshot) {
                                    for (messageSnapshot in dataSnapshot2.children) {
                                        val message = messageSnapshot.getValue(Message::class.java)
                                        if (message != null && message.uid_receiver == userId) {
                                            message.messageType = "receiver"
                                            // Додавайте отримані повідомлення на початок списку
                                            messages.add(0, message)
                                        }
                                    }

                                    // Сортування повідомлень за датою від старіших до новіших
                                    val sortedMessages = messages.sortedBy { it.time }

                                    // Тепер у вас є список повідомлень, відсортований за датою
                                    callback(sortedMessages)
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    callback(emptyList()) // Якщо сталася помилка, повертаємо пустий список
                                }
                            })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        callback(emptyList()) // Якщо сталася помилка, повертаємо пустий список
                    }
                })
        }
    }
}
